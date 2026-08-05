export type TechArticle = {
  id: string;
  title: string;
  category: string;
  readTime: string;
  date: string;
  summary: string;
  content: string;
};

export type VaanService = {
  id: string;
  title: string;
  subtitle: string;
  description: string;
  platforms: string[];
  details: string[];
  accentColor: string;
};

/** Ported verbatim from `articlesList` in Screens.kt. */
export const articlesList: TechArticle[] = [
  {
    id: 'fabric_analytics',
    title: 'Unlocking Real-Time Analytics with Microsoft Fabric',
    category: 'Data Platform',
    readTime: '5 min read',
    date: 'June 25, 2026',
    summary:
      'Learn how to orchestrate low-latency pipeline streams using Microsoft Fabric and Lakehouse storage architectures.',
    content:
      "Microsoft Fabric has emerged as a major player for modern unified enterprise data platforms. By consolidating data lakes, data engineering, data warehouses, and power visualization under a single 'OneLake' SaaS engine, Fabric eliminates the friction of traditional data silos.\n\nKey architectural takeaways for successful implementation:\n1. Unified Storage: OneLake acts as the single source of truth, organizing data in standard Delta Parquet formats.\n2. Medallion Topology: Build Bronze, Silver, and Gold delta tables to systematically clean and refine streams.\n3. Direct Lake Mode: Stream massive datasets from OneLake directly into PowerBI without importing or querying live engines, resulting in a 30% reduction in processing overhead.\n\nOur engineering tests at Vaan show that migrating pipelines to Fabric achieves dramatic reductions in maintenance complexity, specifically for logistics and retail enterprises.",
  },
  {
    id: 'safe_regulated',
    title: 'SAFe 6.0 in Regulated Enterprises: A Practitioner\'s Guide',
    category: 'Bespoke Consulting',
    readTime: '8 min read',
    date: 'May 18, 2026',
    summary:
      'How to systematically scale agile practices, compliance, security, and velocity in highly regulated sectors.',
    content:
      'Implementing agile methodologies like SAFe 6.0 in highly regulated environments (such as banking, automotive, and nuclear energy) is often considered an oxymoron. However, compliance and agility can coexist harmoniously when built on trust and automation.\n\nThree pillars of compliant scale:\n1. Automate Governance: Shift security and automated tests left. Every commit should generate compliance logs.\n2. Lean Portfolio Management (LPM): Align funding directly with strategic value streams rather than rigid projects.\n3. System Demo Involvements: Ensure regulatory compliance officers attend quarterly demos to provide continuous feedback rather than late audits.\n\nBy adopting a value-stream mindset, banking groups have successfully improved development throughput while satisfying stringent audit requirements.',
  },
  {
    id: 'mainframe_postgres',
    title: 'Migrating Mainframe DB2 to PostgreSQL with Zero Downtime',
    category: 'Cloud & Digital',
    readTime: '12 min read',
    date: 'April 05, 2026',
    summary:
      'A detailed technical guide on real-time CDC replication strategies on AWS RDS and GCP Cloud SQL.',
    content:
      "Mainframe modernization requires extreme care, especially when migrating core operational databases. Traditional 'lift and shift' operations fail due to business disruption. The solution lies in Change Data Capture (CDC) replication topologies.\n\nWe recommend the following migration pathway:\n1. Schema Conversion: Use Schema Conversion tools to convert legacy DB2 schemas, indexing, and store procedures to PostgreSQL.\n2. Real-Time Synchronization: Establish a CDC sync layer (e.g., using GCP DMS or AWS DMS) to replicate transaction logs continuously.\n3. Dual-Run Phase: Run both systems in parallel, routing a percentage of query traffic to the cloud Postgres target.\n4. Cutover: Switch the write endpoint to PostgreSQL once replication lag hits sub-millisecond levels.\n\nOur teams routinely deliver migrations that reduce annual licensing fees by up to 70% while improving transaction speeds in financial portfolios.",
  },
  {
    id: 'mobile_offline_first',
    title: 'Designing Robust Offline-First Mobile Architectures',
    category: 'Mobile Dev',
    readTime: '8 min read',
    date: 'June 18, 2026',
    summary:
      'How to build resilient Android applications using Room Database, Kotlin Flow, and WorkManager sync engines.',
    content:
      'Mobile apps built for field operations or enterprise logistics must remain fully functional regardless of connectivity status. Traditional app designs rely heavily on synchronous REST APIs, which break immediately under poor cellular coverage.\n\nOur recommended offline-first architecture:\n1. Local Storage First: All user read and write actions interact strictly with the local Room Database. The UI observes database flows.\n2. Scheduled Syncing: Utilize Android WorkManager to schedule background sync tasks that retry automatically when connectivity is restored.\n3. Conflict Resolution Strategy: Implement deterministic timestamp-based or client-wins policies to handle concurrent database writes.\n4. Network Compression: Compress outgoing synchronization payloads using Protobuf or GZIP to reduce bandwidth overhead.\n\nBy prioritizing local database flows, applications improve latency to near-zero milliseconds and maintain complete functional stability in remote areas.',
  },
  {
    id: 'ai_auto_categorization',
    title: 'AI Support Automation using LLMs and Structured Parsing',
    category: 'Web Dev',
    readTime: '7 min read',
    date: 'February 22, 2026',
    summary:
      'Streamline client support queues by building automated text categorization pipelines with LLMs like Gemini.',
    content:
      'Modern support teams are often overwhelmed by bulk support inquiries. Generative AI can assist by classifying incoming tickets, extracting actionable metadata (company names, products), and drafting high-quality email replies inside customized enterprise web platforms.\n\nThe processing architecture:\n1. Ingestion: Catch inquiries from webhooks or SMTP listeners.\n2. Structured Prompting: Prompt models with structured JSON schemas to enforce output standards (e.g. sentiment score, urgency, category).\n3. Human-in-the-Loop Review: Present the drafted responses to human operators inside a responsive web dashboard for verification before sending.\n\nThis automated approach ensures critical enterprise inquiries receive initial professional responses in less than one minute, resulting in higher retention.',
  },
];

/** Ported from `servicesList` in Screens.kt. */
export const servicesList: VaanService[] = [
  {
    id: 'data_platform',
    title: 'Data Platform Architecture & Strategy',
    subtitle: 'Modern Warehouses & Low-Latency Pipelines',
    description:
      'Transform raw enterprise data streams into decision-ready business intelligence. We design scalable analytics pipelines and secure data lakehouses using the latest cloud warehouse platforms.',
    platforms: ['Snowflake', 'Databricks', 'Microsoft Fabric', 'PostgreSQL'],
    details: [
      'Real-time ETL/ELT pipeline design and streaming telemetry',
      'Modern data lakehouse deployment (Delta Lake and Parquet)',
      'Database modernizations & zero-downtime DB migrations',
      'Analytical reporting optimization & cost control',
    ],
    accentColor: '#34D399',
  },
  {
    id: 'cloud_transform',
    title: 'Cloud & Digital Transformation',
    subtitle: 'Multi-Cloud Strategy (AWS, Azure, GCP)',
    description:
      'We design resilient, secure, and cost-optimised cloud backends. From containerisation to serverless deployments, we orchestrate zero-downtime migrations that scale under regulatory compliance.',
    platforms: ['AWS', 'Azure', 'GCP', 'Kubernetes'],
    details: [
      'Multi-region architecture design with active-active scaling',
      'Infrastructure as Code (IaC) using Terraform and Ansible',
      'Kubernetes container orchestration (EKS, AKS, GKE)',
      'Compliance alignment & FinOps cloud cost optimisation',
    ],
    accentColor: '#38BDF8',
  },
  {
    id: 'mobile_dev',
    title: 'Mobile Application Development',
    subtitle: 'High-Performance Native & Cross-Platform Apps',
    description:
      'We build secure, offline-first mobile solutions. Our engineering ensures smooth background synchronization, local encryption, and highly responsive user interfaces with Jetpack Compose.',
    platforms: ['Android', 'Kotlin', 'Jetpack Compose', 'SQLite / Room'],
    details: [
      'Offline-first databases with background network sync engines',
      'Secure local storage, biometric auth, and encryption',
      'Highly responsive Material 3 reactive animations',
      'Play Store publishing and telemetry monitoring pipelines',
    ],
    accentColor: '#2DD4BF',
  },
  {
    id: 'web_dev',
    title: 'Web Application Development',
    subtitle: 'Enterprise Web Applications & API Gateways',
    description:
      'Build scalable, secure web services and customer portals. We optimize backend microservices and construct modern web applications designed for high load.',
    platforms: ['React', 'TypeScript', 'Node.js', 'GraphQL / REST APIs'],
    details: [
      'Low-latency reactive single page applications (SPAs)',
      'Secure API integration with high-performance cache layers',
      'Role-based authentication & enterprise SSO integrations',
      'Automated testing, CI/CD pipelines, and cloud deployments',
    ],
    accentColor: '#FB923C',
  },
  {
    id: 'bespoke_consulting',
    title: 'Bespoke Technology Consulting / Other',
    subtitle: 'Enterprise Agility, Mentoring & Due Diligence',
    description:
      'Bridging the gap between software developers and executive vision. We coach teams on agile delivery, product ownership, and lean portfolio management (SAFe 6.0) to maximize value.',
    platforms: ['SAFe 6', 'Agile Coaching', 'Tech Audits', 'Mentorship'],
    details: [
      'SAFe 6.0 transformation roadmap formulation & training',
      'Lean Portfolio Management (LPM) alignment & funding workshops',
      'Agile Release Train (ART) facilitation & PI Planning preparation',
      'Architecture due diligence and technical audit reports',
    ],
    accentColor: '#A78BFA',
  },
];
