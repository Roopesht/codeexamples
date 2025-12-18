# Capstone Project Requirements  
## Enterprise HR Policies AI Assistant

### 1. Project Objective
Build a domain-specific AI assistant that answers HR policy questions accurately, cites official policy sources, and avoids assumptions.

The assistant must behave like an HR helpdesk system, not a general chatbot.

---

### 2. Domain Scope
The assistant shall operate strictly within HR policies of a single fictional company.

Included areas:
- Leave policies
- Attendance and work hours
- Holidays
- Remote / hybrid work
- Code of conduct
- Onboarding and exit procedures

Excluded areas:
- Payroll calculations
- Legal interpretation
- Performance management
- Personal employee data

---

### 3. Knowledge Base Requirements
- Use 5–8 HR policy documents.
- Documents must be text-based and structured.
- Each document must have clear headings and sections.
- Policies must be internally consistent.

---

### 4. Retrieval Requirements
The assistant must retrieve relevant policy content before generating answers.

Allowed approaches:
- Semantic retrieval using embeddings and vector store, OR
- Keyword / section-based retrieval without vector store

Full-document prompting is not allowed.

---

### 5. Assistant Behavior Rules
- Responses must be limited to retrieved policy context.
- No guessing or policy invention.
- Neutral, professional HR tone.
- Must clearly refuse when policy does not define an answer.

---

### 6. Structured Output (Mandatory)
Every response must include:
- Answer
- Source Policy (document + section)
- Notes or conditions (if applicable)
- Confidence level (High / Medium / Low)

---

### 7. Clarification Handling
If a question is ambiguous or incomplete:
- The assistant must ask a clarifying question.
- The assistant must not provide a partial or assumed answer.

---

### 8. Tooling Requirement
Use at least one HR-relevant tool:
- Policy category classifier
- Eligibility checker (rule-based)
- Date calculation utility

Tool usage must be explicit and intentional.

---

### 9. Safety and Reliability
- No response without citing a policy section.
- Must say “Not defined in policy” when applicable.
- No legal guarantees or advice.

---

### 10. Evaluation
Test with:
- Valid HR questions
- Ambiguous questions
- Out-of-scope questions

Evaluate correctness, refusal behavior, and tone consistency.

---

### 11. Deliverables
- Runnable assistant
- HR policy documents
- Retrieval logic description
- Prompt and tool definitions
- README with scope and limitations
