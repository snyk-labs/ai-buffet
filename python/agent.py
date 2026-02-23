import os
from smolagents.memory import Memory
from smolagents.llms import OpenAIWrapper
from smolagents.tools import PythonREPLTool, DuckDuckGoSearchTool
from smolagents.agents import ZeroShotAgent

# Initialize memory for the agent
memory = Memory()

# Initialize the language model wrapper
llm = OpenAIWrapper(api_key=os.environ.get("OPENAI_KEY"), model_name="gpt-4")

# Initialize the tools
repl_tool = PythonREPLTool()
search_tool = DuckDuckGoSearchTool()
tools = [repl_tool, search_tool]

# Initialize the ZeroShotAgent
agent = ZeroShotAgent(
    llm=llm,
    memory=memory,
    tools=tools,
    verbose=True  # Set to True for more detailed output
)

# Define a task for the agent
task = "Calculate the square root of 144 and then multiply the result by 5."

# Run the agent with the task
result = agent.run(task)

# Print the result
print(f"\nResult: {result}")
