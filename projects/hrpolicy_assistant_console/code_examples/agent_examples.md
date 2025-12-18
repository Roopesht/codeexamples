# LangGraph ReAct Agent with OpenAI
# This example shows how to create a simple agent that can use tools

from langgraph.prebuilt import create_react_agent
from langchain_openai import ChatOpenAI
from langchain_core.tools import tool

# Define a tool using the @tool decorator
@tool
def get_weather(city: str) -> str:
    """Get weather for a city. Use this when user asks about weather."""
    return f"It's always sunny in {city}!"

# Create the OpenAI model
model = ChatOpenAI(model="gpt-4o-mini", temperature=0)

# Create agent with tools
# Note: The prompt parameter is used for system message
agent = create_react_agent(
    model=model,
    tools=[get_weather]
)

# Run the agent
print("=" * 60)
print("LANGGRAPH REACT AGENT DEMO WITH OPENAI")
print("=" * 60)

result = agent.invoke({
    "messages": [{"role": "user", "content": "what is the weather in sf"}]
})

# Print the conversation
print("\nConversation:")
print("-" * 60)
for message in result["messages"]:
    if hasattr(message, 'content'):
        role = message.__class__.__name__
        print(f"\n{role}:")
        print(message.content)
    if hasattr(message, 'tool_calls') and message.tool_calls:
        print(f"\nTool Calls: {message.tool_calls}")
print("\n" + "=" * 60)



----
