import java.util.*;


class Server {
    int id;
    double trueLatency;        // Sunucunun o anki gerçek (gizli) gecikme süresi
    double estimatedLatency;   // Algoritmanın geçmiş verilere dayanarak yaptığı tahmin
    int selectionCount;        // Bu sunucunun kaç kez seçildiği

    public Server(int id, double baseLatency) {
        this.id = id;
        this.trueLatency = baseLatency;
        this.estimatedLatency = 0.0;
        this.selectionCount = 0;
    }

    // Gürültülü ve Zamanla Değişen (Non-stationary) yanıt süresi üretir
    public double getResponse() {
        Random rand = new Random();
        // Gürültü (Noise): Her isteğin yanıt süresi küçük sapmalar gösterir
        double noise = rand.nextGaussian() * 5;
        // Kayma (Drift): Sunucunun performansı zamanla iyileşebilir veya kötüleşebilir
        double drift = (rand.nextDouble() - 0.5) * 2;
        this.trueLatency += drift;

        return Math.max(5, this.trueLatency + noise); // Latency negatif olamaz
    }
}

class SoftmaxLoadBalancer {
    private List<Server> servers;
    private double tau; // Sıcaklık parametresi (Temperature)

    public SoftmaxLoadBalancer(List<Server> servers, double tau) {
        this.servers = servers;
        this.tau = tau;
    }

    public Server selectServer() {
        int n = servers.size();
        double[] qValues = new double[n];

        // Ödül Fonksiyonu: Gecikme süresi (latency) düşük olanın ödülü yüksektir.
        // Softmax genellikle ödül maksimizasyonu için kullanılır, bu yüzden -latency alıyoruz.
        for (int i = 0; i < n; i++) {
            qValues[i] = -servers.get(i).estimatedLatency;
        }

        // --- TEKNİK ÇÖZÜM: NÜMERİK STABİLİTE (NUMERICAL STABILITY) ---
        // Math.exp(x) işleminde x çok büyürse sonuç 'Infinity' (Overflow) olur.
        // Giriş değerlerinden maksimum değeri çıkararak (x - maxQ) sonucu normalize ediyoruz.
        // Bu işlem matematiksel olasılık sonucunu değiştirmez ancak taşmayı önler.
        double maxQ = Arrays.stream(qValues).max().getAsDouble();

        double[] expValues = new double[n];
        double sumExp = 0;
        for (int i = 0; i < n; i++) {
            // Softmax Formülü: P(i) = exp(Q_i / tau) / sum(exp(Q_j / tau))
            expValues[i] = Math.exp((qValues[i] - maxQ) / tau);
            sumExp += expValues[i];
        }

        // Olasılık dağılımına göre seçim (Roulette Wheel Selection)
        double r = Math.random();
        double cumulativeProb = 0;
        for (int i = 0; i < n; i++) {
            double prob = expValues[i] / sumExp;
            cumulativeProb += prob;
            if (r <= cumulativeProb) return servers.get(i);
        }
        return servers.get(n - 1);
    }

    // Hareketli ortalama ile sunucu performans tahminini güncelleme
    public void update(Server server, double observedLatency) {
        server.selectionCount++;
        double alpha = 1.0 / server.selectionCount; // Artımlı öğrenme oranı
        server.estimatedLatency += alpha * (observedLatency - server.estimatedLatency);
    }
}

public class LoadBalancerSystem {
    public static void main(String[] args) {
        // K adet sunucudan oluşan küme (Cluster)
        List<Server> servers = new ArrayList<>();
        servers.add(new Server(1, 100.0)); // Standart sunucu
        servers.add(new Server(2, 150.0)); // Yavaş sunucu
        servers.add(new Server(3, 80.0));  // En hızlı sunucu (Favori)
        servers.add(new Server(4, 120.0)); // Orta seviye sunucu

        // Softmax Load Balancer (Tau = 5.0 keşif ve sömürü dengesi için)
        SoftmaxLoadBalancer lb = new SoftmaxLoadBalancer(servers, 5.0);

        System.out.println("Sistem Başlatıldı: Softmax ile Yük Dağıtımı Yapılıyor...");
        double totalLatency = 0;

        // 1000 adet istek göndererek sistemi test ediyoruz
        for (int i = 1; i <= 1000; i++) {
            Server selected = lb.selectServer();
            double latency = selected.getResponse();
            lb.update(selected, latency);
            totalLatency += latency;

            if (i % 200 == 0) {
                System.out.printf("%d. İstek -> Kümülatif Ortalama Gecikme: %.2f ms\n", i, (totalLatency / i));
            }
        }

        // Analiz Çıktısı
        System.out.println("\n--- Final Analiz Raporu ---");
        for (Server s : servers) {
            System.out.println("Sunucu ID: " + s.id +
                    " | Seçilme Sayısı: " + s.selectionCount +
                    " | Tahmin Edilen Performans: " + String.format("%.2f", s.estimatedLatency) + " ms");
        }
    }
}