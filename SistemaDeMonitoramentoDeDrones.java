import java.sql.*;
import java.util.*;
import java.util.Date;

abstract class Usuario {
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

    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        this.nome = nome;
    }
    public String getLogin() { return login; }
    public String getSenha() { return senha; } 

    public boolean autenticar(String providedLogin, String providedSenha) {
        return this.login.equals(providedLogin) && this.senha.equals(providedSenha); 
    }
}

class Administrador extends Usuario {
    public Administrador(int id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    public void cadastrarArea(AreaAgricola area) {
        AreaAgricolaDAO.cadastrar(area);
    }

    public void cadastrarDrone(Drone drone) {
        DroneDAO.cadastrar(drone);
    }

    public Relatorio gerarRelatorio(AreaAgricola area) {
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

    public String getId() { return id; }
    public List<String> getSensoresDisponiveis() { return sensoresDisponiveis; }
    public String getStatus() { return status; }
    public int getBateria() { return bateria; }
}

class MissaoVoo {
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
        return MissaoVooDAO.verificarSobreposicao(this.droneId, this.data);
    }

    public void executarMissao() {
        if (!DroneDAO.obterDrone(this.droneId).verificarChecklist()) {
            throw new IllegalStateException("Checklist falhou");
        }
        DadosColetados dados = new DadosColetados(/* params */);
        if (!dados.validarDados()) {
            throw new IllegalStateException("Dados inválidos");
        }
        DadosColetadosDAO.cadastrar(dados, this.id);
    }

    public int getId() { return id; }
}

class DadosColetados {
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

    public int getId() { return id; }
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
        return "Relatório: " + ultimasMedicoes;
    }
}

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
        String sql = "SELECT * FROM USUARIO WHERE login = ? AND senha = ?"; 
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
        String sql = "INSERT INTO AREA_AGRICOLA (id, tamanho, localizacao, tipoCultivo) VALUES (?, ?, ?, ?)";
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
        String sql = "INSERT INTO DRONE (id, status, bateria) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, drone.getId());
            stmt.setString(2, drone.getStatus());
            stmt.setInt(3, drone.getBateria());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   /* public static Drone obterDrone(String id) {
        return null;
    }*/
}

class MissaoVooDAO {
    public static void cadastrar(MissaoVoo missao) {
        String sql = "INSERT INTO MISSAO_VOO (id, data, status, drone_id, area_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, missao.getId());
            stmt.setDate(2, new java.sql.Date(missao.getData().getTime()));
            stmt.setString(3, missao.getStatus());
            stmt.setString(4, missao.getDroneId());
            stmt.setInt(5, missao.getAreaId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean verificarSobreposicao(String droneId, Date data) {
        String sql = "SELECT COUNT(*) FROM MISSAO_VOO WHERE drone_id = ? AND data = ?";
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
        String sql = "INSERT INTO DADOS_COLETADOS (id, temperatura, umidade, pragas, missao_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dados.getId());
            stmt.setDouble(2, dados.getTemperatura());
            stmt.setDouble(3, dados.getUmidade());
            stmt.setString(4, dados.getPragas());
            stmt.setInt(5, missaoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

/*class RelatorioDAO {
    public static Relatorio gerarParaArea(int areaId) {
        return null;
    }
}*/

public class Main {
    public static void main(String[] args) {
        // Autenticação
        if (UsuarioDAO.autenticar("operador", "senha123")) {
            OperadorDrone op = new OperadorDrone(1, "Op", "operador", "senha123");
            MissaoVoo missao = new MissaoVoo(1, new Date(), Arrays.asList("temp"), "agendada", "drone1", 1);
            op.agendarMissao(missao);
            missao.executarMissao();
        }
    }

}


