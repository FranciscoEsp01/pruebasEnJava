/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.myproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class MonitorAreaCalor extends JFrame {

    private final PanelSensores panelSensores;
    private final PanelDashboard panelDashboard;
    private final PanelRegistro panelRegistro;
    private final PanelGrafico panelGrafico;
    private final Timer timer;
    private boolean monitoreoActivo = true;

    // Paleta de colores SaaS Moderno (Estilo Linear)
    private static final Color BG_MAIN = Color.decode("#0A0A0A");
    private static final Color BG_PANEL = Color.decode("#121212");
    private static final Color BORDER_COLOR = Color.decode("#272727");
    private static final Color TEXT_PRIMARY = Color.decode("#F1F1F1");
    private static final Color TEXT_SECONDARY = Color.decode("#8A8A93");
    private static final Color ACCENT_COLOR = Color.decode("#5E6AD2");
    private static final Color DANGER_COLOR = Color.decode("#E05252");

    public MonitorAreaCalor() {
        setTitle("Viernes System - Enterprise Thermal Monitoring");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        // Configuración de fuentes globales
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 12));

        panelRegistro = new PanelRegistro();
        panelGrafico = new PanelGrafico();
        panelSensores = new PanelSensores(40, 60, panelRegistro, panelGrafico);
        panelDashboard = new PanelDashboard(panelSensores);

        JPanel panelCentral = new JPanel(new BorderLayout(20, 20));
        panelCentral.setBackground(BG_MAIN);
        panelCentral.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelCentral.add(panelSensores, BorderLayout.CENTER);
        panelCentral.add(panelGrafico, BorderLayout.SOUTH);

        add(panelDashboard, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelRegistro, BorderLayout.EAST);

        timer = new Timer(250, e -> {
            if (monitoreoActivo) {
                panelSensores.actualizarSimulacionFisica();
                panelDashboard.actualizarMetricas(
                        panelSensores.getMaxTemp(),
                        panelSensores.getPromedioTemp(),
                        panelSensores.getZonasCriticas()
                );
            }
        });
        timer.start();

        setLocationRelativeTo(null);
        configurarMenu();
    }

    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BG_PANEL);
        menuBar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setForeground(TEXT_PRIMARY);
        JMenuItem itemExportar = new JMenuItem("Exportar a CSV...");
        itemExportar.addActionListener(e -> panelRegistro.agregarLog("SYS", "Exportación CSV simulada completada."));
        menuArchivo.add(itemExportar);
        
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MonitorAreaCalor().setVisible(true));
    }

    class PanelDashboard extends JPanel {
        private final JLabel lblMaxTemp;
        private final JLabel lblPromedio;
        private final JLabel lblZonasCriticas;
        private final JButton btnToggle;

        public PanelDashboard(PanelSensores sensores) {
            setLayout(new BorderLayout());
            setBackground(BG_PANEL);
            setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 15));
            statsPanel.setBackground(BG_PANEL);

            lblMaxTemp = crearMetrica("Temperatura Pico", "0.0 °C");
            lblPromedio = crearMetrica("Media Global", "0.0 °C");
            lblZonasCriticas = crearMetrica("Nodos Críticos", "0");

            statsPanel.add(lblMaxTemp);
            statsPanel.add(lblPromedio);
            statsPanel.add(lblZonasCriticas);

            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
            controlPanel.setBackground(BG_PANEL);

            btnToggle = new JButton("Pausar Sistema");
            estilarBoton(btnToggle, true);
            btnToggle.addActionListener(e -> toggleMonitoreo());

            controlPanel.add(btnToggle);

            add(statsPanel, BorderLayout.CENTER);
            add(controlPanel, BorderLayout.EAST);
        }

        private JLabel crearMetrica(String titulo, String valorInicial) {
            JLabel label = new JLabel(String.format("<html><div style='color:%s; font-size:10px;'>%s</div><div style='color:%s; font-size:18px; font-weight:bold; margin-top:2px;'>%s</div></html>", 
                    colorToHex(TEXT_SECONDARY), titulo.toUpperCase(), colorToHex(TEXT_PRIMARY), valorInicial));
            return label;
        }

        public void actualizarMetricas(double max, double prom, int criticos) {
            lblMaxTemp.setText(String.format("<html><div style='color:%s; font-size:10px;'>TEMPERATURA PICO</div><div style='color:%s; font-size:18px; font-weight:bold; margin-top:2px;'>%.1f °C</div></html>", 
                    colorToHex(TEXT_SECONDARY), colorToHex(max > 85 ? DANGER_COLOR : TEXT_PRIMARY), max));
            lblPromedio.setText(String.format("<html><div style='color:%s; font-size:10px;'>MEDIA GLOBAL</div><div style='color:%s; font-size:18px; font-weight:bold; margin-top:2px;'>%.1f °C</div></html>", 
                    colorToHex(TEXT_SECONDARY), colorToHex(TEXT_PRIMARY), prom));
            lblZonasCriticas.setText(String.format("<html><div style='color:%s; font-size:10px;'>NODOS CRÍTICOS</div><div style='color:%s; font-size:18px; font-weight:bold; margin-top:2px;'>%d</div></html>", 
                    colorToHex(TEXT_SECONDARY), colorToHex(criticos > 0 ? DANGER_COLOR : TEXT_PRIMARY), criticos));
        }

        private void toggleMonitoreo() {
            monitoreoActivo = !monitoreoActivo;
            if (monitoreoActivo) {
                btnToggle.setText("Pausar Sistema");
                estilarBoton(btnToggle, true);
                panelRegistro.agregarLog("SYS", "Adquisición de datos reanudada.");
            } else {
                btnToggle.setText("Reanudar Sistema");
                estilarBoton(btnToggle, false);
                panelRegistro.agregarLog("SYS", "Adquisición de datos pausada.");
            }
        }

        private void estilarBoton(JButton btn, boolean activo) {
            btn.setBackground(activo ? BORDER_COLOR : ACCENT_COLOR);
            btn.setForeground(TEXT_PRIMARY);
            btn.setFocusPainted(false);
            btn.setBorder(new EmptyBorder(8, 16, 8, 16));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        private String colorToHex(Color color) {
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }
    }

    class PanelSensores extends JPanel {
        private final int filas;
        private final int columnas;
        private double[][] matrizActual;
        private double[][] matrizSiguiente;
        private final PanelRegistro registro;
        private final PanelGrafico grafico;
        
        private double maxTemp = 0;
        private double promedioTemp = 0;
        private int zonasCriticas = 0;

        private Point sensorHover = null;

        public PanelSensores(int filas, int columnas, PanelRegistro registro, PanelGrafico grafico) {
            this.filas = filas;
            this.columnas = columnas;
            this.registro = registro;
            this.grafico = grafico;
            this.matrizActual = new double[filas][columnas];
            this.matrizSiguiente = new double[filas][columnas];

            setBackground(BG_PANEL);
            setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
            ));

            inicializarAmbiente();

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int w = getWidth() - 20;
                    int h = getHeight() - 20;
                    int cellW = w / columnas;
                    int cellH = h / filas;
                    
                    int col = (e.getX() - 10) / cellW;
                    int fila = (e.getY() - 10) / cellH;

                    if (fila >= 0 && fila < filas && col >= 0 && col < columnas) {
                        sensorHover = new Point(col, fila);
                    } else {
                        sensorHover = null;
                    }
                    repaint();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    sensorHover = null;
                    repaint();
                }
            });
        }

        private void inicializarAmbiente() {
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    matrizActual[i][j] = 22.0; 
                }
            }
        }

        // Simulación de dispersión de calor (Ecuación del calor 2D)
        public void actualizarSimulacionFisica() {
            double alpha = 0.1; 
            maxTemp = 0;
            double suma = 0;
            zonasCriticas = 0;

            // Generar fuentes de calor aleatorias (equipos encendiéndose/apagándose)
            if (Math.random() > 0.6) {
                int f = (int) (Math.random() * filas);
                int c = (int) (Math.random() * columnas);
                matrizActual[f][c] += 40 + Math.random() * 50; 
            }

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    double centro = matrizActual[i][j];
                    double arriba = (i == 0) ? centro : matrizActual[i - 1][j];
                    double abajo = (i == filas - 1) ? centro : matrizActual[i + 1][j];
                    double izq = (j == 0) ? centro : matrizActual[i][j - 1];
                    double der = (j == columnas - 1) ? centro : matrizActual[i][j + 1];

                    // Difusión
                    double nuevoValor = centro + alpha * (arriba + abajo + izq + der - 4 * centro);
                    
                    // Enfriamiento ambiental
                    nuevoValor -= 0.5; 
                    
                    if (nuevoValor < 20.0) nuevoValor = 20.0 + (Math.random() * 2);
                    if (nuevoValor > 120.0) nuevoValor = 120.0;

                    matrizSiguiente[i][j] = nuevoValor;

                    if (nuevoValor > maxTemp) maxTemp = nuevoValor;
                    suma += nuevoValor;
                    if (nuevoValor >= 85.0) zonasCriticas++;
                }
            }

            // Reportar anomalías al log de forma controlada
            if (zonasCriticas > 0 && Math.random() > 0.8) {
                registro.agregarLog("ALERTA", zonasCriticas + " sensores superan umbral de 85°C.");
            }

            // Intercambiar buffers
            double[][] temp = matrizActual;
            matrizActual = matrizSiguiente;
            matrizSiguiente = temp;

            promedioTemp = suma / (filas * columnas);
            grafico.agregarDato(maxTemp, promedioTemp);
            repaint();
        }

        public double getMaxTemp() { return maxTemp; }
        public double getPromedioTemp() { return promedioTemp; }
        public int getZonasCriticas() { return zonasCriticas; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 20;
            int h = getHeight() - 20;
            int cellW = w / columnas;
            int cellH = h / filas;

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    double temp = matrizActual[i][j];
                    g2d.setColor(obtenerColorTermico(temp));
                    
                    int x = 10 + j * cellW;
                    int y = 10 + i * cellH;
                    
                    g2d.fillRect(x, y, cellW, cellH);

                    if (temp >= 85.0) {
                        g2d.setColor(new Color(255, 255, 255, 100));
                        g2d.drawRect(x, y, cellW - 1, cellH - 1);
                    }
                }
            }

            // Dibujar Tooltip flotante si hay hover
            if (sensorHover != null) {
                int col = sensorHover.x;
                int fila = sensorHover.y;
                double temp = matrizActual[fila][col];
                
                String tooltip = String.format(" Sensor [%d,%d]: %.1f °C ", fila, col, temp);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fm = g2d.getFontMetrics();
                int tw = fm.stringWidth(tooltip);
                int th = fm.getHeight();
                
                int mx = 10 + col * cellW + cellW/2;
                int my = 10 + fila * cellH - th;
                
                g2d.setColor(new Color(20, 20, 20, 220));
                g2d.fillRoundRect(mx - tw/2, my - th, tw + 10, th + 4, 6, 6);
                g2d.setColor(BORDER_COLOR);
                g2d.drawRoundRect(mx - tw/2, my - th, tw + 10, th + 4, 6, 6);
                g2d.setColor(Color.WHITE);
                g2d.drawString(tooltip, mx - tw/2 + 5, my - 4);
            }
        }

        private Color obtenerColorTermico(double temp) {
            float minTemp = 20f;
            float maxTempLocal = 100f;
            float normalizada = (float) Math.max(0, Math.min(1, (temp - minTemp) / (maxTempLocal - minTemp)));
            
            // Mapeo tipo Blackbody/Ironbow: Negro -> Azul -> Rojo -> Amarillo -> Blanco
            if (normalizada < 0.25f) {
                return new Color(0, (int)(normalizada * 4 * 100), (int)(normalizada * 4 * 255));
            } else if (normalizada < 0.5f) {
                float n = (normalizada - 0.25f) * 4;
                return new Color((int)(n * 255), 100 + (int)(n * 50), 255 - (int)(n * 255));
            } else if (normalizada < 0.75f) {
                float n = (normalizada - 0.5f) * 4;
                return new Color(255, 150 - (int)(n * 150), 0);
            } else {
                float n = (normalizada - 0.75f) * 4;
                return new Color(255, (int)(n * 255), (int)(n * 255));
            }
        }
    }

    class PanelGrafico extends JPanel {
        private final LinkedList<Double> historicoMax = new LinkedList<>();
        private final LinkedList<Double> historicoProm = new LinkedList<>();
        private final int MAX_PUNTOS = 100;

        public PanelGrafico() {
            setPreferredSize(new Dimension(0, 150));
            setBackground(BG_PANEL);
            setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
            ));
        }

        public void agregarDato(double max, double prom) {
            historicoMax.add(max);
            historicoProm.add(prom);
            if (historicoMax.size() > MAX_PUNTOS) {
                historicoMax.removeFirst();
                historicoProm.removeFirst();
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (historicoMax.isEmpty()) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 20;
            int h = getHeight() - 20;

            // Dibujar rejilla
            g2d.setColor(BORDER_COLOR);
            for (int i = 0; i <= 4; i++) {
                int y = 10 + i * (h / 4);
                g2d.drawLine(10, y, 10 + w, y);
            }

            double escalaX = (double) w / (MAX_PUNTOS - 1);
            double maxGraphValue = 120.0; 
            double escalaY = h / maxGraphValue;

            Path2D pathMax = new Path2D.Double();
            Path2D pathProm = new Path2D.Double();

            for (int i = 0; i < historicoMax.size(); i++) {
                double x = 10 + i * escalaX;
                double yMax = 10 + h - (historicoMax.get(i) * escalaY);
                double yProm = 10 + h - (historicoProm.get(i) * escalaY);

                if (i == 0) {
                    pathMax.moveTo(x, yMax);
                    pathProm.moveTo(x, yProm);
                } else {
                    pathMax.lineTo(x, yMax);
                    pathProm.lineTo(x, yProm);
                }
            }

            g2d.setStroke(new BasicStroke(2f));
            g2d.setColor(DANGER_COLOR);
            g2d.draw(pathMax);

            g2d.setColor(ACCENT_COLOR);
            g2d.draw(pathProm);

            // Leyenda
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.setColor(DANGER_COLOR);
            g2d.drawString("Max Temp", 15, 20);
            g2d.setColor(ACCENT_COLOR);
            g2d.drawString("Media Temp", 80, 20);
        }
    }

    class PanelRegistro extends JPanel {
        private final JTextArea areaTexto;
        private final SimpleDateFormat formatoHora;

        public PanelRegistro() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(320, 0));
            setBackground(BG_PANEL);
            setBorder(new MatteBorder(0, 1, 0, 0, BORDER_COLOR));

            JLabel lblTitulo = new JLabel(" ACTIVITY LOG");
            lblTitulo.setForeground(TEXT_SECONDARY);
            lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 11));
            lblTitulo.setBorder(new EmptyBorder(15, 15, 10, 15));
            add(lblTitulo, BorderLayout.NORTH);

            areaTexto = new JTextArea();
            areaTexto.setEditable(false);
            areaTexto.setBackground(BG_PANEL);
            areaTexto.setForeground(TEXT_PRIMARY);
            areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
            areaTexto.setLineWrap(true);
            areaTexto.setWrapStyleWord(true);
            areaTexto.setMargin(new Insets(0, 15, 15, 15));

            JScrollPane scrollPane = new JScrollPane(areaTexto);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setBackground(BG_PANEL);
            
            add(scrollPane, BorderLayout.CENTER);

            formatoHora = new SimpleDateFormat("HH:mm:ss");
            agregarLog("SYS", "Viernes System Inicializado. Motor de simulación en línea.");
        }

        public void agregarLog(String tag, String mensaje) {
            String hora = formatoHora.format(new Date());
            String colorTag = tag.equals("ALERTA") ? "⚠️" : "ℹ️";
            areaTexto.append(String.format("[%s] %s %s\n\n", hora, colorTag, mensaje));
            areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
        }
    }
}