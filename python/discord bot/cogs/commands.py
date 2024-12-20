import discord
from discord.ext import commands
from discord import app_commands
from config import current_guild


class botCommands(commands.Cog):
    def __init__(self, bot):
        self.bot = bot

    @commands.hybrid_command(name='hello', description='hello (*´ω｀*)', with_app_command=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def hello(self, ctx: commands.Context) -> None:
        username = str(ctx.author).split('#')[0]
        await ctx.send(f'whats good {username}')

    @commands.hybrid_command(name='shutdown', description='shuts down', with_app_command=True, )
    @commands.has_permissions(administrator=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def shutdown(self, ctx: commands.Context) -> None:
        username = str(ctx.author).split('#')[0]
        await ctx.send(f'closing logs...')
        self.bot.logs_file.close()
        await ctx.send(f'logs closed')
        await ctx.send(f'shutting down, see you later {username}')
        await self.bot.close()

    @commands.hybrid_command(name='checkguilds', description='checkguilds', with_app_command=True)
    @commands.has_permissions(administrator=True)
    @app_commands.guilds(discord.Object(id=current_guild))
    async def checkguilds(self, ctx: commands.Context) -> None:
        username = str(ctx.author).split('#')[0]
        await ctx.send(f'{username}, im now in {len(self.bot.guilds)} guilds')
