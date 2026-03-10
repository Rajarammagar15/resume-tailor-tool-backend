# AI Resume Tailor – Backend (Spring Boot + OpenAI)

AI Resume Tailor is an **AI-powered Resume Analyzer and Resume Builder** built with **Java 21 and Spring Boot**.

The backend system analyzes resumes against job descriptions, generates AI-optimized resume content, and produces professional PDF resumes using customizable templates.

The system integrates **OpenAI LLMs**, structured prompt engineering, and a **template-based PDF rendering engine** to deliver high-quality resume outputs.

---

# Overview

The backend provides the core services for the AI Resume Tailor platform, including:

- Resume analysis using AI
- Resume generation from structured user input
- Resume optimization based on job descriptions
- PDF resume generation using multiple templates
- Structured resume data management

The system is designed using **clean layered architecture** and **design patterns** to ensure maintainability and extensibility.

---

# Key Features

## Resume Analyzer (Resume + Job Description)

Users can upload an existing resume along with a job description.

The system performs the following steps:

1. Extract text from the uploaded PDF resume.
2. Send the resume content and job description to the AI model.
3. Analyze the resume against job requirements.
4. Return structured insights and recommendations.

### Analysis Results

The analysis includes:

- AI match score
- Keyword match percentage
- Matched skills
- Missing skills
- Resume improvement suggestions
- AI-generated structured resume content

---

## Resume Builder (User Input → AI Resume)

Users can generate a resume by providing structured information such as:

- Header
- Skills
- Experience
- Projects
- Education
- Certifications
- Optional job description

The AI generates:

- Professional summary
- Experience bullet points
- Project descriptions

The system then produces a **structured resume object** that can be exported as a professional PDF.

---

## Multiple Resume Templates

The system supports multiple PDF resume templates.

Available templates include:

- Modern
- Corporate
- Compact

Templates are implemented using the **Template Strategy Pattern**, making it easy to add additional resume styles.

---

## Dynamic Resume Section Ordering

The system dynamically adjusts resume section ordering based on candidate experience level.

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

This ensures the resume remains **ATS-friendly and recruiter-focused**.

---

## AI Prompt Engineering

Custom prompts enforce strict resume writing rules.

Bullet points follow the structure:

```
Action Verb + Feature + Technology + Impact
```

Example:

```
Developed a scalable REST API using Spring Boot that reduced response latency by 40%.
```

Additional safeguards include:

- Prevent hallucinated technologies
- Preserve user-provided company names
- Maintain original project names
- Preserve work experience accuracy
- Return **strict JSON responses only**

---

# Tech Stack

## Backend Framework

- Java 21
- Spring Boot 3
- Spring WebFlux WebClient
- Lombok

## AI Integration

- OpenAI Chat Completions API
- Prompt Engineering

## PDF Generation

- OpenPDF

## Architecture

- Clean Layered Architecture
- Template Strategy Pattern
- Template Method Pattern

---

# Project Structure

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

# API Endpoints

## Analyze Resume

Uploads a resume and analyzes it against a job description.

```
POST /api/v1/analyze
```

### Request

Multipart request:

- `resumeFile`
- `jobDescription`

### Example Response

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

## Generate Resume from User Input

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

## Generate Resume PDF

```
GET /api/v1/pdf/{analysisId}?template=MODERN
```

Supported templates:

- `MODERN`
- `CORPORATE`
- `COMPACT`

---

# Resume Template Architecture

The resume templates follow the **Strategy Pattern**.

```
TemplateRenderer
      │
BaseTemplateRenderer
      │
 ├── ModernTemplateRenderer
 ├── CorporateTemplateRenderer
 └── CompactTemplateRenderer
```

Each template customizes:

- Fonts
- Colors
- Layout
- Section styling

---

## Resume Rendering Pipeline

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

## AI Integration Flow

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

# Handling AI Output Reliability

To ensure stable processing of AI responses:

- AI responses must return strict JSON
- Responses are parsed using Jackson ObjectMapper
- Mapping uses index-based alignment rather than name matching

Example:

```java
project.setBullets(aiProjects.get(i).getBullets());
```

This prevents failures when AI slightly modifies project names.

---

# Connection Management

The OpenAI WebClient is configured with:

- Connection pooling
- Idle connection eviction
- Response timeouts
- Retry handling

This prevents connection errors such as:

```
java.net.SocketException: Connection reset
```

---

# Running the Project

## Clone Repository

```bash
git clone https://github.com/yourusername/ai-resume-tailor-backend.git
```

## Configure OpenAI API Key

In `application.yml`:

```yaml
openai:
  api-key: YOUR_API_KEY
```

## Start Application

```bash
mvn spring-boot:run
```

Server runs at:

```
http://localhost:8080
```

---

# Future Improvements

Potential future enhancements include:

- Resume preview endpoint
- ATS score visualization
- Keyword highlighting
- Job description aware resume section ordering
- Resume editing capabilities
- AI bullet rewriting
- Job posting link scraping
- Multiple resume versions

---

# Author

**Rajaram Magar**

Backend Engineer | Java | Spring Boot | AI Systems

- GitHub: [https://github.com/Rajarammagar15](https://github.com/Rajarammagar15)
- LinkedIn: [https://www.linkedin.com/in/rajaram-magar](https://www.linkedin.com/in/rajaram-magar)

---

# Disclaimer

This repository contains a personal project developed independently for learning and experimentation purposes.

This project is not affiliated with, endorsed by, or representing any employer or client organization of the author.

No proprietary code, confidential information, or internal resources from any employer were used in the development of this project.

All code and content in this repository are the author's own work.