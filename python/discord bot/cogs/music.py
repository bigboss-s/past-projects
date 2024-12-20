import re
import discord
import lavalink
from discord.ext import commands
from discord import app_commands
from config import current_guild

url_rx = re.compile(r'https?://(?:www\.)?.+')


def format_zeros(time: str):
    if len(time) < 2:
        return '0' + time
    else:
        return time


def convert_track_time(milis):
    seconds = int(milis / 1000)
    minutes = int(seconds / 60)
    hours = int(minutes / 60)
    seconds = seconds % 60
    minutes = minutes % 60
    return f'{hours}:{format_zeros(str(minutes))}:{format_zeros(str(seconds))}' if hours > 0 else f'{minutes}:{format_zeros(str(seconds))}'


class LavalinkVoiceClient(discord.VoiceClient):
    def __init__(self, client: discord.Client, channel: discord.abc.Connectable):
        self.client = client
        self.channel = channel
        if hasattr(self.client, 'lavalink'):
            self.lavalink = self.client.lavalink
        else:
            self.client.lavalink = lavalink.Client(client.user.id)
            self.client.lavalink.add_node(
                'localhost',
                7000,
                'coolbot',
                'jp',
                'default-node'
            )
            self.lavalink = self.client.lavalink
        self.trackevent = lavalink.TrackStartEvent

    async def on_voice_server_update(self, data):
        lavalink_data = {
            't': 'VOICE_SERVER_UPDATE',
            'd': data
        }
        await self.lavalink.voice_update_handler(lavalink_data)

    async def on_voice_state_update(self, data):
        lavalink_data = {
            't': 'VOICE_STATE_UPDATE',
            'd': data
        }
        await self.lavalink.voice_update_handler(lavalink_data)

    async def connect(self, *, timeout: float, reconnect: bool, self_deaf: bool = False,
                      self_mute: bool = False) -> None:
        self.lavalink.player_manager.create(guild_id=self.channel.guild.id)
        await self.channel.guild.change_voice_state(channel=self.channel, self_mute=self_mute, self_deaf=True)

    async def disconnect(self, *, force: bool = False) -> None:
        player = self.lavalink.player_manager.get(self.channel.guild.id)

        if not force and not player.is_connected:
            return

        await self.channel.guild.change_voice_state(channel=None)

        player.channel_id = None
        self.cleanup()


class Music(commands.Cog):
    def __init__(self, bot):
        self.bot = bot

        if not hasattr(bot, 'lavalink'):
            bot.lavalink = lavalink.Client(bot.user.id)
            bot.lavalink.add_node('localhost', 7000, 'coolbot', 'jp', 'music-node')

        lavalink.add_event_hook(self.track_hook)

    async def cog_before_invoke(self, ctx):
        guild_check = ctx.guild is not None
        if guild_check:
            await self.ensure_voice(ctx)

        return guild_check

    async def cog_command_error(self, ctx, error):
        if isinstance(error, commands.CommandInvokeError):
            await ctx.send(error.original)

    async def ensure_voice(self, ctx):
        username = str(ctx.author).split('#')[0]
        player = self.bot.lavalink.player_manager.create(ctx.guild.id)
        should_connect = ctx.command.name in ('play',)

        if not ctx.author.voice or not ctx.author.voice.channel:
            raise commands.CommandInvokeError(f'{username}, youre not in a voice channel!')

        v_client = ctx.voice_client
        if not v_client:
            if not should_connect:
                raise commands.CommandInvokeError('im not in a voice channel!')

            player.store('channel', ctx.channel.id)
            await ctx.author.voice.channel.connect(cls=LavalinkVoiceClient)
        else:
            if v_client.channel.id != ctx.author.voice.channel.id:
                raise commands.CommandInvokeError(f'{username}, i have to be in your voice channel!')

    async def track_hook(self, event):
        if isinstance(event, lavalink.events.QueueEndEvent):
            guild_id = event.player.guild_id
            guild = self.bot.get_guild(guild_id)
            await guild.voice_client.disconnect(force=True)
        if isinstance(event, lavalink.events.TrackStartEvent):
            if not event.player.is_playing:
                return
            ctx = self.bot.get_channel(event.player.fetch('channel'))
            username = await self.bot.fetch_user(event.track.requester)
            username = str(username.name).split('#')[0]
            embed = discord.Embed(colour=discord.Color.dark_magenta(), title=f'**now playing**',
                                  description=f'[{event.track.title}]({event.track.uri}) ({convert_track_time(event.track.duration)})')
            if len(event.player.queue) == 0:
                embed.set_footer(
                    text=f'this is the last song in the queue! leaving after it ends\nrequested by {username}')
            elif len(event.player.queue) == 1:
                embed.set_footer(text=f'with {len(event.player.queue)} song in the queue\nrequested by {username}')
            else:
                embed.set_footer(text=f'with {len(event.player.queue)} songs in the queue\nrequested by {username}')
            await ctx.send(embed=embed)

    @commands.hybrid_command(name='play', description='searches (yt) or plays a song or a playlist from url (yt or sc)',
                             with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def play(self, ctx, *, query: str):
        username = str(ctx.author).split('#')[0]
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)
        await ctx.send(f'searching for {username}\'s query...')
        print(f'searching for {query} with:')
        query = query.strip('<>')

        if not url_rx.match(query):
            query = f'ytsearch:{query}'
        results = await player.node.get_tracks(query)

        if not results or not results.tracks:
            return await ctx.send(f'sorry {username}, but i couldnt find anything!')

        #   TRACK_LOADED    - single video/direct URL
        #   PLAYLIST_LOADED - direct URL to playlist
        #   SEARCH_RESULT   - query prefixed with either ytsearch: or scsearch:.
        #   NO_MATCHES      - query yielded no results
        #   LOAD_FAILED     - most likely, the video encountered an exception during loading.
        if results.load_type == 'PLAYLIST_LOADED':
            print('playlist type')
            tracks = results.tracks

            for track in tracks:
                player.add(requester=ctx.author.id, track=track)

            embed = discord.Embed(colour=discord.Color.dark_magenta(), title=f'{username}\'s playlist added to the queue', description=f'{results.playlist_info.name} - {len(tracks)} tracks')
            await ctx.channel.send(embed=embed)

        elif results.load_type == 'TRACK_LOADED':
            print('direct url type')
            track = results.tracks[0]
            if player.is_playing != 0:
                embed = discord.Embed(colour=discord.Color.dark_magenta(), title=f'{username}\'s song added to the queue', description=f'[{track.title}]({track.uri})')
                await ctx.channel.send(embed=embed)
            player.add(requester=ctx.author.id, track=track)

        elif results.load_type == 'SEARCH_RESULT':
            print(f'youtube searched')

            tracks = results['tracks'][0:10]
            i = 0
            query_result = ''
            for track in tracks:
                i = i + 1
                query_result = query_result + f'{i}) [{track["info"]["title"]}]({track["info"]["uri"]}) ({convert_track_time(track.duration)})\n'
            choice_embed = discord.Embed(colour=discord.Color.dark_magenta(),
                                         title='**pick a youtube result to play:**',
                                         description=query_result)
            await ctx.channel.send(embed=choice_embed)

            def check(m):
                return m.author.id == ctx.author.id and m.channel == ctx.channel

            response = await self.bot.wait_for('message', check=check)
            if response.content not in ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10']:
                if not player.is_playing:
                    await ctx.voice_client.disconnect()
                return await ctx.send(f'cancelling {username}\'s search')
            track = tracks[int(response.content) - 1]

            player.add(requester=ctx.author.id, track=track)
            if player.is_playing:
                embed = discord.Embed(colour=discord.Color.dark_magenta(),
                                      title=f'**{username}\'s song added to the queue**',
                                      description=f'[{track.title}]({track.uri})')
                await ctx.channel.send(embed=embed)

        else:
            print('search failed')
            fail_embed = discord.Embed(title='**search failed, try again**')
            return await ctx.send(embed=fail_embed)

        if not player.is_playing:
            await player.play(volume=10)

    @commands.hybrid_command(name='leave', description='leaves voice channel', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def leave(self, ctx):
        username = str(ctx.author).split('#')[0]
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)
        if ctx.voice_client is not None:
            player.queue.clear()
            await player.stop()
            await ctx.voice_client.disconnect()
            return await ctx.send(f'leaving {username} from {ctx.channel}')

        await ctx.send(f'im not in a voice channel!')

    @commands.hybrid_command(name='queue', description='shows queue', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def queue(self, ctx):
        username = str(ctx.author).split('#')[0]
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)
        if len(player.queue) == 0:
            return await ctx.send(f'{username}, the queue is empty!')
        queue_list = ''
        i = 1
        for track in player.queue:
            queue_list += f'{i}. [{track.title}]({track.uri})\n'
            i = i + 1
        embed = discord.Embed(colour=discord.Color.dark_magenta(),
                              title='**current queue**',
                              description=f'{queue_list}')
        await ctx.send(embed=embed)

    @commands.hybrid_command(name='skip', description='skips current song', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def skip(self, ctx):
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)
        username = str(ctx.author).split('#')[0]
        if not player.is_playing:
            return await ctx.send(f'{username}, im currently not playing anything!')

        if ctx.author.voice is None:
            return await ctx.send(f'{username}, you are not in a voice channel!')
        if len(player.queue) == 0:
            await ctx.send(f'{username}, you skipped the last song!')
            return await self.leave(ctx)
        await player.play()
        await ctx.send(f'skipped!')

    @commands.hybrid_command(name='pause', description='pauses', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def pause(self, ctx):
        username = str(ctx.author).split('#')[0]
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)
        await player.set_pause(True)
        await ctx.send(f'{username} paused')

    @commands.hybrid_command(name='resume', description='resumes', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def resume(self, ctx):
        username = str(ctx.author).split('#')[0]
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)
        await player.set_pause(False)
        await ctx.send(f'{username} resumed')

    @commands.hybrid_command(name='stop', description='stops and clears queue', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def stop(self, ctx):
        username = str(ctx.author).split('#')[0]
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)

        if ctx.author.voice is None:
            return await ctx.send(f'{username}, you are not in a voice channel!')

        player.queue.clear()
        await player.stop()
        await ctx.send(f'{username} stopped!')

    @commands.hybrid_command(name='clearqueue', description='clears queue', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def clear_queue(self, ctx):
        username = str(ctx.author).split('#')[0]
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)
        player.queue.clear()
        await ctx.send(f'{username} cleared the queue!')

    @commands.hybrid_command(name='nowplaying', description='shows current song and progress', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def now_playing(self, ctx):
        player = self.bot.lavalink.player_manager.get(ctx.guild.id)
        username = await self.bot.fetch_user(player.current.requester)
        username = str(username.name).split('#')[0]
        np_embed = discord.Embed(colour=discord.Color.dark_magenta(),
                                 title='now playing',
                                 description=f'[{player.current.title}]({player.current.uri})\n'
                                             f'{convert_track_time(player.position)} / {convert_track_time(player.current.duration)}')
        np_embed.set_footer(text=f'requested by {username}')
        await ctx.send(embed = np_embed)
