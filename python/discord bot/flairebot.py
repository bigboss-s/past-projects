import discord
from discord.ext import commands
from cogs.music import Music
from cogs.commands import botCommands
from pathlib import Path
from config import current_guild

cogs = [Music, botCommands]

TOKEN = ''


class bot(commands.Bot):
    def __init__(self):
        intents = discord.Intents.default()
        intents.message_content = True
        super().__init__(intents=intents, command_prefix='!')
        self.logs_file = open('logs.txt', 'a', encoding='utf-8')

    async def setup_hook(self):
        await nicebot.add_cog(botCommands(nicebot))
        await nicebot.add_cog(Music(nicebot))
        await self.tree.sync(guild=discord.Object(id=current_guild))
        print(f'synced as {self.user}')


nicebot = bot()


@nicebot.event
async def on_ready():
    print('Logged in as {0.user}'.format(nicebot))


@nicebot.event
async def on_message(message):
    username = str(message.author).split('#')[0]
    user_message = message.content
    channel = message.channel.name
    message_full = f'{username}: {user_message} ({channel})'
    print(message_full)
    nicebot.logs_file.write(f'{message.created_at} {message_full} \n')

    if message.author == nicebot.user:
        return

    await nicebot.process_commands(message)


if not Path('token.txt').is_file():
    TOKEN = input(f'token file missing, please provide a token: ')
    while True:
        saveToken = input(f'save token? y/n \n')
        if saveToken == 'y':
            with open('token.txt', 'a') as tokenFile:
                tokenFile.write(TOKEN)
            tokenFile.close()
            print(tokenFile.closed)
            break
        elif saveToken == 'n':
            break
        else:
            print(f'invalid input, ')
else:
    with open('token.txt') as tokenFile:
        TOKEN = (tokenFile.read())
    tokenFile.close()
print(f'running with token: {TOKEN}')

nicebot.run(TOKEN)


