from mcp.server.fastmcp import FastMCP

mcp = FastMCP("DemoServer")

@mcp.tool()
def add(a: int, b: int) -> int:
    """Add two numbers"""
    return a + b


@mcp.resource("hello://{name}")
def get_greeting(name: str) -> str:
    """Get a personalized hello message"""
    return f"Hello, {name}!"
