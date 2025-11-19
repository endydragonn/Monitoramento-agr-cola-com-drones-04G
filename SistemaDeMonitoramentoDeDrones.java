import java.sql.*;
import java.util.*;
import java.util.Date;

interface Autenticavel {
    boolean autenticar(String login, String senha);
}

interface Validavel {
    boolean validar();
}

abstract class Usuario implements Autenticavel {
    private int id;
    private String nome;
    private String login;
    private String senha;

    public Usuario(int id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
    }

    // Getters and Setters with validation
    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        this.nome = nome;
    }
    public String getLogin() { return login; }
    public String getSenha() { return senha; } // Senha não deve ser alterada diretamente

    @Override
    public boolean autenticar(String providedLogin, String providedSenha) {
        return this.login.equals(providedLogin) && this.senha.equals(providedSenha); // Em produção, usar hash
    }
}

class Administrador extends Usuario {
    public Administrador(int id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    public void cadastrarArea(AreaAgricola area) {
        // Lógica para cadastrar área no DB
        AreaAgricolaDAO.cadastrar(area);
    }

    public void cadastrarDrone(Drone drone) {
        // Lógica para cadastrar drone no DB
        DroneDAO.cadastrar(drone);
    }

    public Relatorio gerarRelatorio(AreaAgricola area) {
        // Lógica para gerar relatório do DB
        return RelatorioDAO.gerarParaArea(area.getId());
    }
}

class OperadorDrone extends Usuario {
    public OperadorDrone(int id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    public void agendarMissao(MissaoVoo missao) {
        if (!missao.validarSobreposicao()) {
            throw new IllegalStateException("Missão sobreposta detectada");
        }
        MissaoVooDAO.cadastrar(missao);
    }
}

class AreaAgricola {
    private int id;
    private double tamanho;
    private String localizacao;
    private String tipoCultivo;

    public AreaAgricola(int id, double tamanho, String localizacao, String tipoCultivo) {
        this.id = id;
        this.tamanho = tamanho;
        this.localizacao = localizacao;
        this.tipoCultivo = tipoCultivo;
    }

    // Getters
    public int getId() { return id; }
    public double getTamanho() { return tamanho; }
    public String getLocalizacao() { return localizacao; }
    public String getTipoCultivo() { return tipoCultivo; }
}

class Drone {
    private String id;
    private List<String> sensoresDisponiveis;
    private String status;
    private int bateria;

    public Drone(String id, List<String> sensores, String status, int bateria) {
        this.id = id;
        this.sensoresDisponiveis = sensores;
        this.status = status;
        this.bateria = bateria;
    }

    public boolean verificarChecklist() {
        return bateria >= 20 && !sensoresDisponiveis.isEmpty(); // Exemplo de checklist
    }

    // Getters
    public String getId() { return id; }
    public List<String> getSensoresDisponiveis() { return sensoresDisponiveis; }
    public String getStatus() { return status; }
    public int getBateria() { return bateria; }
}

class MissaoVoo implements Validavel {
    private int id;
    private Date data;
    private List<String> sensoresUtilizados;
    private String status;
    private String droneId;
    private int areaId;

    public MissaoVoo(int id, Date data, List<String> sensores, String status, String droneId, int areaId) {
        this.id = id;
        this.data = data;
        this.sensoresUtilizados = sensores;
        this.status = status;
        this.droneId = droneId;
        this.areaId = areaId;
    }

    public boolean validarSobreposicao() {
        // Consultar DB para verificar sobreposições
        return MissaoVooDAO.verificarSobreposicao(this.droneId, this.data);
    }

    public void executarMissao() {
        Drone drone = DroneDAO.obterDrone(this.droneId);
        if (!drone.verificarChecklist()) {
            throw new IllegalStateException("Checklist falhou");
        }
        // Simular dados coletados
        List<String> imagens = Arrays.asList("img1.jpg", "img2.jpg");
        DadosColetados dados = new DadosColetados(1, imagens, 25.0, 60.0, "Sem pragas", this.id);
        if (!dados.validar()) {
            throw new IllegalStateException("Dados inválidos");
        }
        DadosColetadosDAO.cadastrar(dados, this.id);
    }

    @Override
    public boolean validar() {
        return !sensoresUtilizados.isEmpty() && data != null;
    }

    // Getters
    public int getId() { return id; }
    public Date getData() { return data; }
    public String getDroneId() { return droneId; }
    public int getAreaId() { return areaId; }
    public String getStatus() { return status; }
}

class DadosColetados implements Validavel {
    private int id;
    private List<String> imagens;
    private double temperatura;
    private double umidade;
    private String pragas;
    private int missaoId;

    public DadosColetados(int id, List<String> imagens, double temp, double umid, String pragas, int missaoId) {
        this.id = id;
        this.imagens = imagens;
        this.temperatura = temp;
        this.umidade = umid;
        this.pragas = pragas;
        this.missaoId = missaoId;
    }

    public boolean validarDados() {
        return temperatura >= -50 && temperatura <= 50 && umidade >= 0 && umidade <= 100 && !imagens.isEmpty();
    }

    @Override
    public boolean validar() {
        return validarDados();
    }

    // Getters
    public int getId() { return id; }
    public double getTemperatura() { return temperatura; }
    public double getUmidade() { return umidade; }
    public String getPragas() { return pragas; }
}

class Relatorio {
    private String ultimasMedicoes;
    private List<MissaoVoo> voosRealizados;
    private int areaId;

    public Relatorio(String medicoes, List<MissaoVoo> voos, int areaId) {
        this.ultimasMedicoes = medicoes;
        this.voosRealizados = voos;
        this.areaId = areaId;
    }

    public String gerar() {
        // Lógica para formatar relatório
        return "Relatório: " + ultimasMedicoes + "\nVoos: " + voosRealizados.size();
    }
}

// Exemplo de DAO com integração ao BD e PreparedStatements
class ConnectionFactory {
    private static final String URL = "jdbc:postgresql://localhost:5432/drone_db";
    private static final String USER = "user";
    private static final String PASS = "pass";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

class UsuarioDAO {
    public static boolean autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuario WHERE login = ? AND senha = ?"; // Senha deve ser hashed
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

class AreaAgricolaDAO {
    public static void cadastrar(AreaAgricola area) {
        String sql = "INSERT INTO area_agricola (id, tamanho, localizacao, tipo_cultivo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, area.getId());
            stmt.setDouble(2, area.getTamanho());
            stmt.setString(3, area.getLocalizacao());
            stmt.setString(4, area.getTipoCultivo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class DroneDAO {
    public static void cadastrar(Drone drone) {
        String sql = "INSERT INTO drone (id, status, bateria) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, drone.getId());
            stmt.setString(2, drone.getStatus());
            stmt.setInt(3, drone.getBateria());
            stmt.executeUpdate();
            // Inserir sensores em drone_sensor
            for (String sensor : drone.getSensoresDisponiveis()) {
                String sensorSql = "INSERT INTO drone_sensor (drone_id, sensor) VALUES (?, ?)";
                try (PreparedStatement sensorStmt = conn.prepareStatement(sensorSql)) {
                    sensorStmt.setString(1, drone.getId());
                    sensorStmt.setString(2, sensor);
                    sensorStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Drone obterDrone(String id) {
        String sql = "SELECT * FROM drone WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                int bateria = rs.getInt("bateria");
                // Obter sensores
                List<String> sensores = new ArrayList<>();
                String sensorSql = "SELECT sensor FROM drone_sensor WHERE drone_id = ?";
                try (PreparedStatement sensorStmt = conn.prepareStatement(sensorSql)) {
                    sensorStmt.setString(1, id);
                    ResultSet sensorRs = sensorStmt.executeQuery();
                    while (sensorRs.next()) {
                        sensores.add(sensorRs.getString("sensor"));
                    }
                }
                return new Drone(id, sensores, status, bateria);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class MissaoVooDAO {
    public static void cadastrar(MissaoVoo missao) {
        String sql = "INSERT INTO missao_voo (id, data, status, drone_id, area_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, missao.getId());
            stmt.setDate(2, new java.sql.Date(missao.getData().getTime()));
            stmt.setString(3, missao.getStatus());
            stmt.setString(4, missao.getDroneId());
            stmt.setInt(5, missao.getAreaId());
            stmt.executeUpdate();
            // Inserir sensores em missao_sensor
            for (String sensor : missao.sensoresUtilizados) {
                String sensorSql = "INSERT INTO missao_sensor (missao_id, sensor) VALUES (?, ?)";
                try (PreparedStatement sensorStmt = conn.prepareStatement(sensorSql)) {
                    sensorStmt.setInt(1, missao.getId());
                    sensorStmt.setString(2, sensor);
                    sensorStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean verificarSobreposicao(String droneId, Date data) {
        String sql = "SELECT COUNT(*) FROM missao_voo WHERE drone_id = ? AND data = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, droneId);
            stmt.setDate(2, new java.sql.Date(data.getTime()));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

class DadosColetadosDAO {
    public static void cadastrar(DadosColetados dados, int missaoId) {
        String sql = "INSERT INTO dados_coletados (id, temperatura, umidade, pragas, missao_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dados.getId());
            stmt.setDouble(2, dados.getTemperatura());
            stmt.setDouble(3, dados.getUmidade());
            stmt.setString(4, dados.getPragas());
            stmt.setInt(5, missaoId);
            stmt.executeUpdate();
            // Inserir imagens em dados_imagem
            for (String imagem : dados.imagens) {
                String imgSql = "INSERT INTO dados_imagem (dados_id, imagem) VALUES (?, ?)";
                try (PreparedStatement imgStmt = conn.prepareStatement(imgSql)) {
                    imgStmt.setInt(1, dados.getId());
                    imgStmt.setString(2, imagem);
                    imgStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class RelatorioDAO {
    public static Relatorio gerarParaArea(int areaId) {
        String medicoes = ""; // Placeholder para query
        List<MissaoVoo> voos = new ArrayList<>();
        // Query exemplo para últimas medições
        String sql = "SELECT d.temperatura, d.umidade FROM dados_coletados d JOIN missao_voo m ON d.missao_id = m.id WHERE m.area_id = ? ORDER BY m.data DESC LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                medicoes = "Temp: " + rs.getDouble("temperatura") + ", Umid: " + rs.getDouble("umidade");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Query para voos
        String voosSql = "SELECT * FROM missao_voo WHERE area_id = ? ORDER BY data DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(voosSql)) {
            stmt.setInt(1, areaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Simples, sem sensores por agora
                MissaoVoo voo = new MissaoVoo(rs.getInt("id"), rs.getDate("data"), new ArrayList<>(), rs.getString("status"), rs.getString("drone_id"), areaId);
                voos.add(voo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Relatorio(medicoes, voos, areaId);
    }
}

// Exemplo de uso baseado no diagrama de sequência
public class Main {
    public static void main(String[] args) {
        // Autenticação
        if (UsuarioDAO.autenticar("operador", "senha123")) {
            OperadorDrone op = new OperadorDrone(1, "Op", "operador", "senha123");
            MissaoVoo missao = new MissaoVoo(1, new Date(), Arrays.asList("temp", "umid"), "agendada", "drone1", 1);
            op.agendarMissao(missao);
            missao.executarMissao();
            // Gerar relatório
            AreaAgricola area = new AreaAgricola(1, 100.0, "Fazenda X", "Milho");
            Administrador admin = new Administrador(2, "Admin", "admin", "admin123");
            Relatorio rel = admin.gerarRelatorio(area);
            System.out.println(rel.gerar());
        }
    }
}
