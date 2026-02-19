Bu çalışma, bir dağıtık sistem mühendisi perspektifiyle tasarlanmış olup, heterojen bir sunucu kümesinde toplam bekleme süresini minimize etmeyi hedefleyen akıllı bir yük dengeleyici simülasyonudur.

Projenin temel taşı olan Server sınıfı, gerçek dünya senaryolarındaki ağ belirsizliklerini yansıtmak adına hem anlık gürültülerle hem de zamanla performansın iyileşip kötüleştiği durağan olmayan bir dağıtımla modellenmiştir.

Geleneksel "Round-Robin" veya "Random" gibi statik yöntemlerin aksine, burada uygulanan Softmax Action Selection algoritması, sunucuların geçmiş performans verilerini analiz ederek olasılıksal bir seçim mekanizması sunar.

Algoritma, düşük gecikme süresine sahip sunucuları daha sık seçerek sistemi sömürürken, performansın dinamik olarak değişebileceği ihtimaline karşı diğer sunucuları da belirli aralıklarla test ederek keşif dengesini korur.

Sistemin en kritik teknik başarısı, Softmax hesaplamaları sırasında ortaya çıkabilecek Nümerik Stabilite probleminin profesyonelce çözülmüş olmasıdır.

Üstel fonksiyonların büyük gecikme değerlerinde yol açabileceği sayısal taşma riskine karşı, giriş değerlerinden maksimum değerin çıkarıldığı "Max-Subtraction" yöntemi kullanılmış, böylece yazılımın çökmesi matematiksel olarak engellenmiştir.

Uygulama süresince yapılan çalışma zamanı analizleri, algoritmanın kısa sürede en hızlı sunucuyu tespit ettiğini ve ortalama gecikme süresini kararlı bir seviyeye indirdiğini kanıtlamaktadır.

