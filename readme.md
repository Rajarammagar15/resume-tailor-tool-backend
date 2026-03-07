# AI Resume Tailor & Builder (Spring Boot + OpenAI)

An **AI-powered Resume Analyzer and Resume Builder** built with **Java 21 and Spring Boot** that helps users:

- Analyze resumes against a Job Description (ATS-style analysis)
- Generate tailored resumes using AI
- Build resumes from structured user input
- Generate professional PDFs using multiple templates

The system leverages **OpenAI LLMs**, structured prompts, and a **template-based PDF rendering engine** to produce high-quality resumes.

---

# 🚀 Key Features

## 1. Resume Analyzer (PDF + Job Description)

Users can upload an existing resume and a job description.

The system:

- Extracts text from the PDF
- Sends the resume + job description to the AI model
- Calculates ATS-style metrics
- Returns structured insights

Analysis includes:

- AI match score
- Keyword match percentage
- Missing skills
- Matched skills
- Resume improvement suggestions
- AI-rewritten structured resume

---

## 2. Resume Builder (User Input → AI Resume)

Users can create a resume by providing structured information:

- Header
- Skills
- Experience
- Projects
- Education
- Certifications
- Job Description (optional)

The AI generates:

- Professional summary
- Experience bullet points
- Project bullet points

The system then produces a **structured resume object** and allows exporting it as a PDF.

---

## 3. Multiple Resume Templates

The system supports multiple PDF resume templates:

- Modern
- Corporate
- Compact

Templates are implemented using the **Template Strategy Pattern**, allowing easy addition of new styles.

---

## 4. Dynamic Resume Section Ordering

The system automatically adjusts resume section order depending on the candidate profile.

### Experienced Candidate

```
Summary
Skills
Experience
Projects
Education
Certifications
```

### Fresher Candidate

```
Summary
Skills
Projects
Internships
Education
Certifications
```

This ensures resumes remain **ATS-friendly and recruiter-friendly**.

---

## 5. AI Prompt Engineering

Custom prompts enforce strict resume writing rules.

Bullet format:

```
Action Verb + Feature + Technology + Impact
```

Example:

```
Developed a scalable REST API using Spring Boot that reduced response latency by 40%.
```

Additional safeguards:

- No hallucinated technologies
- Maintain user-provided experience
- Preserve project and company names
- Return **strict JSON only**

---

# 🛠 Tech Stack

## Backend

- Java 21
- Spring Boot 3
- Spring WebFlux WebClient
- Lombok

## AI

- OpenAI Chat Completions API
- Prompt Engineering

## PDF Generation

- OpenPDF

## Architecture

- Template Strategy Pattern
- Template Method Pattern
- Clean layered architecture

---

# 📁 Project Structure

```
com.rajaram.resumetailor
│
├── controller
│   ├── ResumeController
│   └── ResumeBuilderController
│
├── service
│   ├── AIRewriteService
│   ├── ResumeAiService
│   ├── ResumeBuilderService
│   ├── PdfGenerationService
│   ├── PdfParserService
│   └── AnalysisCacheService
│
├── template
│   ├── TemplateRenderer
│   ├── BaseTemplateRenderer
│   ├── ModernTemplateRenderer
│   ├── CorporateTemplateRenderer
│   └── CompactTemplateRenderer
│
├── model
│   ├── StructuredResume
│   ├── Header
│   ├── Skills
│   ├── Experience
│   ├── Project
│   ├── Education
│   └── Certifications
│
├── model.builder
│   ├── ResumeBuilderRequest
│   ├── UserExperience
│   ├── AiResumeResponse
│   └── ExperienceType
│
├── mapper
│   └── ResumeResponseMapper
│
└── config
    └── WebClientConfig
```

---

# 🔌 API Endpoints

## 1. Analyze Resume

Uploads a resume and analyzes it against a job description.

```
POST /api/v1/analyze
```

### Request

Multipart request:

- resumeFile
- jobDescription

### Response

```json
{
  "analysisId": "...",
  "aiScore": 78,
  "keywordScore": 72,
  "matchedSkills": [],
  "missingSkills": [],
  "suggestions": [],
  "resume": {}
}
```

---

## 2. Build Resume from User Input

Generate resume content using AI.

```
POST /api/v1/resume/generate
```

### Request

```json
{
  "header": {},
  "skills": {},
  "yearsOfExperience": 2,
  "experience": [],
  "projects": [],
  "education": [],
  "certifications": [],
  "jobDescription": ""
}
```

### Response

```json
{
  "analysisId": "...",
  "resume": {}
}
```

---

## 3. Generate Resume PDF

```
GET /api/v1/pdf/{analysisId}?template=MODERN
```

Templates supported:

```
MODERN
CORPORATE
COMPACT
```

---

# 🧩 Resume Template Architecture

Templates follow the **Strategy Pattern**.

```
TemplateRenderer
      │
BaseTemplateRenderer
      │
 ├── ModernTemplateRenderer
 ├── CorporateTemplateRenderer
 └── CompactTemplateRenderer
```

BaseTemplateRenderer defines the rendering flow, while individual templates customize:

- Fonts
- Colors
- Layout
- Dividers

---

# ⚙ Resume Rendering Pipeline

```
StructuredResume
        │
        ▼
TemplateRenderer
        │
        ▼
OpenPDF Document
        │
        ▼
Generated Resume PDF
```

---

# 🤖 AI Integration Flow

```
User Input / Resume PDF
        │
        ▼
Prompt Builder
        │
        ▼
OpenAI Chat Completion
        │
        ▼
Structured JSON Response
        │
        ▼
Jackson ObjectMapper
        │
        ▼
StructuredResume
```

---

# 🧠 Handling AI Output Reliability

To ensure stability:

- AI responses must return **strict JSON**
- JSON is parsed using Jackson
- Mapping uses **index-based alignment** instead of name matching
- Prevents failures when AI slightly changes project names

Example mapping approach:

```
project.setBullets(aiProjects.get(i).getBullets());
```

---

# 🌐 Connection Management (Production Ready)

The OpenAI WebClient is configured with:

- Connection pooling
- Idle connection eviction
- Response timeouts
- Retry support

This prevents errors like:

```
java.net.SocketException: Connection reset
```

---

# ▶ Running the Project

### Clone Repository

```
git clone https://github.com/yourusername/ai-resume-tailor.git
```

---

### Configure OpenAI API Key

`application.yml`

```yaml
openai:
  api-key: YOUR_API_KEY
```

---

### Start Application

```
mvn spring-boot:run
```

Server runs at:

```
http://localhost:8080
```

---

# 📈 Future Improvements

Planned enhancements:

- Resume preview endpoint
- ATS score visualization
- Keyword highlighting
- JD-aware resume section ordering
- Resume editing UI
- AI bullet rewriting
- Job link scraping
- Multiple resume versions

---

# 👨‍💻 Author

**Rajaram Magar**

Backend Engineer | Java | Spring Boot | AI Systems

GitHub: https://github.com/Rajarammagar15
LinkedIn: https://www.linkedin.com/in/rajaram-magar

---