from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser

# Define the Prompt Template
prompt = ChatPromptTemplate.from_messages([
    ("system", "You are a helpful assistant that translates {input_language} to {output_language}."),
    ("human", "{text}")
])

# LangChain automatically uses the API key from the environment variable
openai = ChatOpenAI(model="gpt-3.5-turbo", temperature=0.7)
gemini = None
# Initialize the Output Parser
output_parser = StrOutputParser()

# Build the LCEL Chain using the pipe (|) operator
chain = prompt | openai | output_parser

# Invoke the chain
result = chain.invoke({
    "input_language": "English",
    "output_language": "Hindi",
    "text": "I love programming with LangChain!"
})


print(result)




---------
import os
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables.history import RunnableWithMessageHistory
from langchain_community.chat_message_histories import ChatMessageHistory
from google.colab import userdata # Assuming you are still in Colab

# --- Setup (Install libraries if needed, set API key) ---
# !pip install -qqq langchain-openai langchain-core langchain-community

# 1. Model
llm = ChatOpenAI(
    model="gpt-4o-mini",
    temperature=0.7
)

# 2. Prompt (Use MessagesPlaceholder for history injection)
prompt = ChatPromptTemplate.from_messages([
    ("system", "You are a friendly assistant."),
    # This placeholder handles the history correctly in v1.x
    MessagesPlaceholder(variable_name="chat_history"),
    ("human", "{input}")
])

# 3. Core Chain & Memory Store
store = {} # A simple in-memory dictionary acting as our database
output_parser = StrOutputParser()

# The core stateless chain built with LCEL
core_chain = prompt | llm | output_parser

# Function to provide the correct history object based on session ID
def get_session_history(session_id: str) -> ChatMessageHistory:
    if session_id not in store:
        store[session_id] = ChatMessageHistory()
    return store[session_id]

# 4. Wrap the chain with the v1.x history manager
chain_with_history = RunnableWithMessageHistory(
    core_chain,
    get_session_history,
    input_messages_key="input", # Key for the new human input
    history_messages_key="chat_history", # Key used in the prompt template placeholder
)
# 5. Interactions
session_id = "test_session_456"
config = {"configurable": {"session_id": session_id}}

# Interaction 1: Pass a dictionary with the "input" key
r1 = chain_with_history.invoke(
    {"input": "Hi, I'm Roopesh"}, # Corrected input format
    config=config
)
print(f"AI 1st: {r1}")

# Interaction 2: Pass a dictionary again
r2 = chain_with_history.invoke(
    {"input": "What's my name?"}, # Corrected input format
    config=config
)
print(f"AI 2nd: {r2}")

# The store dictionary now holds the conversation history correctly
print("\nFull conversation history:", store[session_id].messages)

Full conversation history: [HumanMessage(content="Hi, I'm Roopesh", additional_kwargs={}, response_metadata={}), AIMessage(content='Hi Roopesh! How can I assist you today?', additional_kwargs={}, response_metadata={}), HumanMessage(content="What's my name?", additional_kwargs={}, response_metadata={}), AIMessage(content='Your name is Roopesh! How can I help you today?', additional_kwargs={}, response_metadata={})]

----

from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser

# Initialize components
llm = ChatOpenAI(model="gpt-3.5-turbo", temperature=0.7)
parser = StrOutputParser()

# Single chained statement
result = (
    ChatPromptTemplate.from_template("Translate to German: {text}") | llm | parser |
    (lambda x: {"text": x}) |  # Wrap output as dict for next chain
    ChatPromptTemplate.from_template("Make this exciting, but in simple language: {text}") | llm | parser |
    (lambda x: {"text": x}) |  # Wrap again
    ChatPromptTemplate.from_template("Say this like actor Amitabh Bachchan: {text}") | llm | parser
).invoke({"text": "The future of AI is exciting"})

print(result)

----

result = chain.invoke({"input_language": "English",
                       "output_language": "Spanish",
                       "text": "Hello"})
print(result)

# 2. batch() - Multiple calls in parallel
results = chain.batch([
    {"input_language": "English", "output_language": "French", "text": "Hello"},
    {"input_language": "English", "output_language": "German", "text": "Hello"},
    {"input_language": "English", "output_language": "Italian", "text": "Hello"}
])
for r in results:
    print(r)

# 3. stream() - Token by token streaming
for chunk in chain.stream({"input_language": "English",
                           "output_language": "Japanese",
                           "text": "Hello"}):
    print(chunk, end="", flush=True)


----

chain_with_history = RunnableWithMessageHistory(
    core_chain,
    get_session_history,
    input_messages_key="input",
    history_messages_key="chat_history",
)

# 6. Have conversations!
session_id = "user_123"
config = {"configurable": {"session_id": session_id}}

# First interaction
response1 = chain_with_history.invoke(
    {"input": "Hi, I'm Roopesh"},
    config=config
)
print(f"AI: {response1}")
# Output: "Hello Roopesh! Nice to meet you. How can I help you today?"

# Second interaction - AI remembers!
response2 = chain_with_history.invoke(
    {"input": "What's my name?"},
    config=config
)
print(f"AI: {response2}")
# Output: "Your name is Roopesh!"

# Check stored history
print("\nHistory:", store[session_id].messages)


----


chain_with_history = RunnableWithMessageHistory(
    core_chain,
    get_session_history,
    input_messages_key="input",
    history_messages_key="chat_history",
)

# 6. Have conversations!
session_id = "user_123"
config = {"configurable": {"session_id": session_id}}

# First interaction
response1 = chain_with_history.invoke(
    {"input": "Hi, I'm Roopesh"},
    config=config
)
print(f"AI: {response1}")
# Output: "Hello Roopesh! Nice to meet you. How can I help you today?"

# Second interaction - AI remembers!
response2 = chain_with_history.invoke(
    {"input": "What's my name?"},
    config=config
)
print(f"AI: {response2}")
# Output: "Your name is Roopesh!"

# Check stored history
print("\nHistory:", store[session_id].messages)



----

from langchain_openai import ChatOpenAI, OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnableParallel, RunnablePassthrough
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import TextLoader

# 1. Load and split documents
loader = TextLoader("company_policies.txt")
documents = loader.load()

text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=500,
    chunk_overlap=50
)
chunks = text_splitter.split_documents(documents)

# 2. Create vector store
embeddings = OpenAIEmbeddings()
vectorstore = FAISS.from_documents(chunks, embeddings)
retriever = vectorstore.as_retriever(search_kwargs={"k": 3})

----


# 3. Define prompt
prompt = ChatPromptTemplate.from_messages([
    ("system", "Answer based on the following context:\n\n{context}"),
    ("human", "{question}")
])

# 4. Helper function to format docs
def format_docs(docs):
    return "\n\n".join(doc.page_content for doc in docs)

# 5. Build the RAG chain with LCEL
model = ChatOpenAI(model="gpt-4o-mini")

rag_chain = (
    RunnableParallel({
        "context": retriever | format_docs,
        "question": RunnablePassthrough()
    })
    | prompt
    | model
    | StrOutputParser()
)

# 6. Ask questions!
answer = rag_chain.invoke("What is the remote work policy?")
print(answer)

------

#from langgraph.prebuilt import create_react_agent
from langchain.agents import create_agent

def get_weather(city: str) -> str:
    """Get weather for a city."""
    return f"It's always sunny in {city}!"

# Create agent with tools
agent = create_agent(
    model="gpt-3.5-turbo",
    tools=[get_weather],
)

# Run the agent
result = agent.invoke({
    "messages": [{"role": "user", "content": "what is the weather in sf"}]
})



-----


