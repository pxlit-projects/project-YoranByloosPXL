# Communicatie tussen de Microservices

### 1. API Gateway
- **API Gateway** werkt als tussenlaag tussen de Angular-frontend en de backend microservices.
- Alle aanvragen van de frontend worden via de gateway naar de juiste service gestuurd.

---

### 2. PostService
- **Synchroon (OpenFeign)**:
  - Vraagt statusinformatie op bij de **ReviewService**, bijvoorbeeld of een post is goedgekeurd.
- **Asynchroon (RabbitMQ)**:
  - Stuurt berichten wanneer een post wordt aangemaakt of aangepast, zodat andere services dit kunnen verwerken.

---

### 3. ReviewService
- **Synchroon (OpenFeign)**:
  - Communiceert met de **PostService** om posts en hun status te beheren.
- **Asynchroon (RabbitMQ)**:
  - Stuurt meldingen wanneer een post is goedgekeurd of afgewezen, zodat andere services hiervan op de hoogte zijn.

---

### 4. CommentService
- **Synchroon (OpenFeign)**:
  - Haalt gegevens over posts op bij de **PostService**, zodat reacties aan de juiste post gekoppeld worden.
- **Asynchroon (RabbitMQ)**:
  - Stuurt meldingen als er nieuwe reacties worden geplaatst.

---

### 5. Messaging Service (RabbitMQ)
- RabbitMQ zorgt voor asynchrone communicatie tussen de microservices.
- Voorbeelden:
  - Een melding van de **PostService** naar **ReviewService** bij een nieuwe post.
  - Een melding van de **ReviewService** naar **PostService** bij goedkeuring of afwijzing van een post.
  - Reacties van **CommentService** worden via RabbitMQ gedeeld.

---

### 6. Config Service & Discovery Service
- **Config Service**:
  - Beheert gedeelde configuraties voor alle microservices.
- **Discovery Service**:
  - Helpt microservices elkaar te vinden, bijvoorbeeld voor OpenFeign-communicatie.
