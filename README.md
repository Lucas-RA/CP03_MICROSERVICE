# CP03 - API REST com Spring Boot

API REST com dois domínios: **Finanças** e **Futebol (Copa do Mundo)**.  
O CP03 refatora o CP02 introduzindo **Service**, **DTOs** e **Mappers**.

---

## O que mudou no CP03

### 1. ID gerado automaticamente pelo banco

Antes o cliente enviava o `id` no body. Agora o banco gera sozinho — o cliente não precisa (e não deve) controlar isso.

```java
// Antes (CP02)
@Id
private Long id;

// Depois (CP03)
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
```

---

### 2. Camada Service

A Controller parou de falar direto com o Repository. A Service passou a ser a intermediária, centralizando a lógica de negócio.

```
CP02: Controller → Repository → Banco
CP03: Controller → Service → Repository → Banco
```

```java
@Service
public class FinancaService {

    @Autowired
    private FinancaRepository repository;

    public Financa createOrUpdate(Financa financa) {
        return repository.save(financa); // JPA decide: INSERT ou UPDATE
    }

    public Optional<Financa> findById(Long id) {
        return repository.findById(id);
    }

    public List<Financa> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
```

> `createOrUpdate` unifica criação e atualização — o JPA detecta automaticamente se o `id` já existe no banco.

---

### 3. DTOs — Data Transfer Object

Na borda da API nunca devemos expor ou receber a Entity JPA diretamente. Os DTOs criam um contrato separado para entrada e saída.

```
Banco  ↔  Entity (model)   → camada de persistência
Cliente ↔  DTO              → camada de borda
```

**Request (entrada) — sem `id`:**
```java
@Data
public class FinancaCreateRequest {
    private String emissor;
    private double taxa;
    private String risco;
    private String vencimento;
    private int quantidade;
}
```

**Response (saída) — com `id`:**
```java
@Data
public class FinancaResponse {
    private Long id;
    private String emissor;
    private double taxa;
    private String risco;
    private String vencimento;
    private int quantidade;
}
```

---

### 4. Mappers com ModelMapper

O Mapper centraliza a conversão entre DTO e Entity. Usa a biblioteca **ModelMapper**, que mapeia automaticamente campos com o mesmo nome — sem conversão manual.

```java
@Component
public class FinancaMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    // CreateRequest → Entity (POST)
    public Financa toModel(FinancaCreateRequest dto) {
        return modelMapper.map(dto, Financa.class);
    }

    // UpdateRequest → Entity, injeta o id que veio pela URL (PUT)
    public Financa toModel(Long id, FinancaUpdateRequest dto) {
        Financa financa = modelMapper.map(dto, Financa.class);
        financa.setId(id);
        return financa;
    }

    // Entity → Response (todos os retornos)
    public FinancaResponse toDTO(Financa entity) {
        return modelMapper.map(entity, FinancaResponse.class);
    }
}
```

> `@Component` em vez de `@Service` porque o Mapper não tem lógica de negócio — só transforma dados.

---

### 5. Controllers refatoradas

```java
@RestController
@RequestMapping("/financas")
public class FinancasController {

    @Autowired
    private FinancaService service;

    @Autowired
    private FinancaMapper financaMapper;

    @PostMapping
    public ResponseEntity<FinancaResponse> create(@RequestBody FinancaCreateRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(financaMapper.toDTO(service.createOrUpdate(financaMapper.toModel(dto))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancaResponse> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(financaMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<FinancaResponse>> findAll() {
        return ResponseEntity.ok(service.findAll()
                .stream()
                .map(financaMapper::toDTO)
                .toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancaResponse> update(@PathVariable Long id,
                                                   @RequestBody FinancaUpdateRequest dto) {
        if (service.findById(id).isPresent()) {
            return ResponseEntity.ok(financaMapper.toDTO(
                    service.createOrUpdate(financaMapper.toModel(id, dto))));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

> O DELETE agora retorna `404` se o recurso não existir — no CP02 sempre retornava `204`.

---

## Dependências adicionadas no `pom.xml`

```xml
<!-- Converte automaticamente entre Entity e DTO -->
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.1.1</version>
</dependency>

<!-- Habilita @NotNull, @NotBlank, @Positive etc. -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## Tecnologias

- Java 17 · Spring Boot 4.0.3 · Spring Data JPA · Spring Validation
- ModelMapper 3.1.1 · MySQL 9.6 · Lombok · SpringDoc OpenAPI · Docker

---

## Rodando o projeto

**1. Subir o banco com Docker:**
```bash
docker run -d \
  --name mysql --rm \
  -e MYSQL_ROOT_PASSWORD=root_pwd \
  -e MYSQL_USER=new_user \
  -e MYSQL_PASSWORD=my_pwd \
  -p 3306:3306 \
  mysql
```

**2. Rodar a aplicação:**
```bash
mvn spring-boot:run
```

**3. Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

---

## Endpoints

### Finanças — `/financas`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/financas` | Lista todas |
| GET | `/financas/{id}` | Busca por ID |
| POST | `/financas` | Cria |
| PUT | `/financas/{id}` | Atualiza |
| DELETE | `/financas/{id}` | Remove |

**POST/PUT body:**
```json
{
  "emissor": "Tesouro Nacional",
  "taxa": 12.5,
  "risco": "baixo",
  "vencimento": "2030-01-01",
  "quantidade": 10
}
```

### Copa do Mundo — `/copa`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/copa` | Lista todas |
| GET | `/copa/{id}` | Busca por ID |
| POST | `/copa` | Cria |
| PUT | `/copa/{id}` | Atualiza |
| DELETE | `/copa/{id}` | Remove |

**POST/PUT body:**
```json
{
  "ano": 2022,
  "capeao": "Argentina",
  "vice": "França",
  "sede": "Qatar",
  "melhorJogador": "Lionel Messi"
}
```

> O campo `id` **não** deve ser enviado — é gerado automaticamente pelo banco.

---

## Estrutura do projeto

```
src/main/java/br/com/fiap/cp01_api01/
├── controller/
│   ├── FinancasController.java
│   └── FutebolController.java
├── dto/
│   ├── FinancaCreateRequest.java
│   ├── FinancaUpdateRequest.java
│   ├── FinancaResponse.java
│   ├── FinancaMapper.java
│   ├── FutebolCreateRequest.java
│   ├── FutebolUpdateRequest.java
│   ├── FutebolResponse.java
│   └── FutebolMapper.java
├── model/
│   ├── Financa.java
│   └── Futebol.java
├── repository/
│   ├── FinancaRepository.java
│   └── FutebolRepository.java
└── service/
    ├── FinancaService.java
    └── FutebolService.java
```
