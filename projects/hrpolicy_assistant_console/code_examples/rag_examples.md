from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.embeddings import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_openai import ChatOpenAI
from langchain_core.runnables import RunnableParallel, RunnablePassthrough

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
#answer = rag_chain.invoke("What is the remote work policy?")
#print(answer)

# Stream the response token by token, Output streams live as it's generated!
for chunk in rag_chain.stream("What is the remote work policy?"):
    print(chunk, end="", flush=True)
#How many leaves can be availed by an employee?


----

