package mx.tecnm.restaurante.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import mx.tecnm.restaurante.dao.ClienteDAO;
import mx.tecnm.restaurante.dao.ProductoDAO;
import mx.tecnm.restaurante.model.Cliente;
import mx.tecnm.restaurante.model.DetallePedido;
import mx.tecnm.restaurante.model.Pedido;
import mx.tecnm.restaurante.model.Producto;
import mx.tecnm.restaurante.service.PedidoService;

public class MenuGrafico extends JFrame {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final PedidoService pedidoService = new PedidoService();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);
    private final JLabel lblEstadoGlobal = new JLabel("Listo • Conectado a PostgreSQL");

    // Modelos de tablas y componentes reutilizables
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloPedidos;
    private DefaultTableModel modeloCarrito;
    private final List<DetallePedido> carritoActual = new ArrayList<>();
    private JTextArea areaHistorial;

    public MenuGrafico() {
        setTitle("Sistema Restaurante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 560));
        setSize(1120, 680);
        setLocationRelativeTo(null);

        // Fondo principal con degradado oscuro limpio
        JPanel fondoDegradado = new JPanel(new BorderLayout(16, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        getWidth(), getHeight(), new Color(30, 27, 75)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        fondoDegradado.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(fondoDegradado);

        fondoDegradado.add(crearSidebarGlass(), BorderLayout.WEST);
        fondoDegradado.add(crearAreaCentralGlass(), BorderLayout.CENTER);
        fondoDegradado.add(crearBarraEstadoGlass(), BorderLayout.SOUTH);

        // Cargar vista inicial
        cargarTablaProductos(false);
    }

    // =========================================================================
    // COMPONENTES VISUALES GLASSMORPHISM
    // =========================================================================

    private JPanel crearPanelCristal(LayoutManager layout, int radio) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Relleno translúcido
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
                // Borde sutil brillante
                g2.setColor(new Color(255, 255, 255, 55));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JPanel crearSidebarGlass() {
        JPanel sidebar = crearPanelCristal(new BorderLayout(0, 12), 22);
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBorder(new EmptyBorder(18, 14, 18, 14));

        JLabel titulo = new JLabel("<html><b>RESTAURANTE</b><br><span style='font-size:10px;color:#94a3b8;'>Gestión JDBC & PostgreSQL</span></html>");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        titulo.setBorder(new EmptyBorder(0, 8, 12, 8));
        sidebar.add(titulo, BorderLayout.NORTH);

        JPanel menuBotones = new JPanel(new GridLayout(9, 1, 0, 8));
        menuBotones.setOpaque(false);

        menuBotones.add(crearBotonMenu("1. Listar productos", () -> {
            cargarTablaProductos(false);
            cardLayout.show(contentCards, "PRODUCTOS");
        }));
        menuBotones.add(crearBotonMenu("2. Disponibles", () -> {
            cargarTablaProductos(true);
            cardLayout.show(contentCards, "PRODUCTOS");
        }));
        menuBotones.add(crearBotonMenu("3. Buscar producto", () -> cardLayout.show(contentCards, "BUSCAR_PROD")));
        menuBotones.add(crearBotonMenu("4. Registrar cliente", () -> cardLayout.show(contentCards, "CLIENTE")));
        menuBotones.add(crearBotonMenu("5. Consultar pedidos", () -> {
            cargarTablaPedidos();
            cardLayout.show(contentCards, "PEDIDOS");
        }));
        menuBotones.add(crearBotonMenu("6. Crear pedido", () -> cardLayout.show(contentCards, "CREAR_PEDIDO")));
        menuBotones.add(crearBotonMenu("7. Total de pedido", () -> cardLayout.show(contentCards, "TOTAL_PEDIDO")));
        menuBotones.add(crearBotonMenu("8. Cambiar estado", () -> cardLayout.show(contentCards, "ESTADO_PEDIDO")));
        menuBotones.add(crearBotonMenu("9. Historial estado", () -> cardLayout.show(contentCards, "HISTORIAL")));

        JScrollPane scrollMenu = new JScrollPane(menuBotones);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.setBorder(null);
        sidebar.add(scrollMenu, BorderLayout.CENTER);

        return sidebar;
    }

    private JButton crearBotonMenu(String texto, Runnable accion) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override
                    public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? new Color(56, 189, 248, 70) : new Color(255, 255, 255, 14));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(new Color(241, 245, 249));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    private JPanel crearAreaCentralGlass() {
        JPanel contenedor = crearPanelCristal(new BorderLayout(), 22);
        contenedor.setBorder(new EmptyBorder(20, 22, 20, 22));

        contentCards.setOpaque(false);
        contentCards.add(vistaProductos(), "PRODUCTOS");
        contentCards.add(vistaBuscarProducto(), "BUSCAR_PROD");
        contentCards.add(vistaRegistrarCliente(), "CLIENTE");
        contentCards.add(vistaConsultarPedidos(), "PEDIDOS");
        contentCards.add(vistaCrearPedido(), "CREAR_PEDIDO");
        contentCards.add(vistaTotalPedido(), "TOTAL_PEDIDO");
        contentCards.add(vistaCambiarEstado(), "ESTADO_PEDIDO");
        contentCards.add(vistaHistorial(), "HISTORIAL");

        contenedor.add(contentCards, BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearBarraEstadoGlass() {
        JPanel barra = crearPanelCristal(new BorderLayout(), 14);
        barra.setBorder(new EmptyBorder(8, 16, 8, 16));
        lblEstadoGlobal.setForeground(new Color(148, 163, 184));
        lblEstadoGlobal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        barra.add(lblEstadoGlobal, BorderLayout.WEST);
        return barra;
    }

    // =========================================================================
    // VISTAS DE LOS 9 CASOS
    // =========================================================================

    private JPanel vistaProductos() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);
        p.add(crearTituloSeccion("Catálogo de Productos"), BorderLayout.NORTH);

        modeloProductos = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Disponible"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = estilizarTabla(new JTable(modeloProductos));
        p.add(envolverScrollGlass(tabla), BorderLayout.CENTER);
        return p;
    }

    private JPanel vistaBuscarProducto() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        p.add(crearTituloSeccion("Buscar Producto por ID"), BorderLayout.NORTH);

        JPanel form = crearFormularioResponsivo();
        JTextField txtId = crearCampoTexto();
        JTextArea resultado = crearAreaResultado();
        JButton btnBuscar = crearBotonAccion("Buscar Producto");

        btnBuscar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                Producto prod = productoDAO.buscarPorId(id);
                if (prod != null) {
                    resultado.setText("Producto encontrado:\n\n" + prod);
                    notificar("Producto #" + id + " encontrado.", false);
                } else {
                    resultado.setText("No existe ningún producto registrado con el ID " + id);
                    notificar("Producto no encontrado.", true);
                }
            } catch (NumberFormatException ex) {
                notificar("Ingresa un ID numérico válido.", true);
            } catch (SQLException ex) {
                manejarErrorSQL(ex);
            }
        });

        agregarFilaForm(form, 0, "ID del Producto:", txtId);
        agregarFilaForm(form, 1, "", btnBuscar);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        centro.add(form, BorderLayout.NORTH);
        centro.add(envolverScrollGlass(resultado), BorderLayout.CENTER);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private JPanel vistaRegistrarCliente() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        p.add(crearTituloSeccion("Registrar Nuevo Cliente"), BorderLayout.NORTH);

        JPanel form = crearFormularioResponsivo();
        JTextField txtNombre = crearCampoTexto();
        JTextField txtApellido = crearCampoTexto();
        JTextField txtTel = crearCampoTexto();
        JTextField txtEmail = crearCampoTexto();
        JButton btnGuardar = crearBotonAccion("Guardar Cliente");

        btnGuardar.addActionListener(e -> {
            try {
                Cliente c = new Cliente(
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        txtTel.getText().trim(),
                        txtEmail.getText().trim()
                );
                clienteDAO.insertar(c);
                notificar("Cliente registrado con éxito.", false);
                txtNombre.setText(""); txtApellido.setText(""); txtTel.setText(""); txtEmail.setText("");
            } catch (SQLException ex) {
                manejarErrorSQL(ex);
            }
        });

        agregarFilaForm(form, 0, "Nombre:", txtNombre);
        agregarFilaForm(form, 1, "Apellido:", txtApellido);
        agregarFilaForm(form, 2, "Teléfono:", txtTel);
        agregarFilaForm(form, 3, "Correo electrónico:", txtEmail);
        agregarFilaForm(form, 4, "", btnGuardar);

        p.add(form, BorderLayout.NORTH);
        return p;
    }

    private JPanel vistaConsultarPedidos() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);
        p.add(crearTituloSeccion("Pedidos Registrados"), BorderLayout.NORTH);

        modeloPedidos = new DefaultTableModel(new String[]{"ID Pedido", "ID Cliente", "Fecha", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = estilizarTabla(new JTable(modeloPedidos));
        p.add(envolverScrollGlass(tabla), BorderLayout.CENTER);
        return p;
    }

    private JPanel vistaCrearPedido() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);
        p.add(crearTituloSeccion("Crear Pedido (Transacción Atómica)"), BorderLayout.NORTH);

        JPanel topForm = crearFormularioResponsivo();
        JTextField txtIdCliente = crearCampoTexto();
        JTextField txtIdProd = crearCampoTexto();
        JTextField txtCant = crearCampoTexto();

        JButton btnAgregar = crearBotonAccion("+ Agregar al pedido");
        JButton btnConfirmar = crearBotonAccion("Confirmar Pedido (Commit)");
        JButton btnLimpiar = crearBotonAccion("Vaciar");

        modeloCarrito = new DefaultTableModel(new String[]{"ID Producto", "Cantidad", "Precio Unit."}, 0);
        JTable tablaCarrito = estilizarTabla(new JTable(modeloCarrito));

        btnAgregar.addActionListener(e -> {
            try {
                int idProd = Integer.parseInt(txtIdProd.getText().trim());
                int cant = Integer.parseInt(txtCant.getText().trim());
                Producto prod = productoDAO.buscarPorId(idProd);
                if (prod == null) {
                    notificar("El producto #" + idProd + " no existe.", true);
                    return;
                }
                carritoActual.add(new DetallePedido(idProd, cant, prod.getPrecio()));
                modeloCarrito.addRow(new Object[]{idProd + " - " + prod.getNombre(), cant, "$" + prod.getPrecio()});
                txtIdProd.setText(""); txtCant.setText("");
                notificar("Producto agregado a la lista.", false);
            } catch (NumberFormatException ex) {
                notificar("ID de producto y cantidad deben ser números enteros.", true);
            } catch (SQLException ex) {
                manejarErrorSQL(ex);
            }
        });

        btnLimpiar.addActionListener(e -> {
            carritoActual.clear();
            modeloCarrito.setRowCount(0);
            notificar("Lista limpiada.", false);
        });

        btnConfirmar.addActionListener(e -> {
            try {
                int idCliente = Integer.parseInt(txtIdCliente.getText().trim());
                int idPedido = pedidoService.crearPedidoCompleto(idCliente, new ArrayList<>(carritoActual));
                carritoActual.clear();
                modeloCarrito.setRowCount(0);
                txtIdCliente.setText("");
                notificar("¡Pedido #" + idPedido + " creado y confirmado (COMMIT)!", false);
            } catch (NumberFormatException ex) {
                notificar("Ingresa un ID de cliente válido.", true);
            } catch (IllegalArgumentException ex) {
                notificar(ex.getMessage(), true);
            } catch (SQLException ex) {
                manejarErrorSQL(ex);
            }
        });

        agregarFilaForm(topForm, 0, "ID del Cliente:", txtIdCliente);
        agregarFilaForm(topForm, 1, "ID Producto:", txtIdProd);
        agregarFilaForm(topForm, 2, "Cantidad:", txtCant);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnLimpiar);
        agregarFilaForm(topForm, 3, "", panelBotones);

        p.add(topForm, BorderLayout.NORTH);
        p.add(envolverScrollGlass(tablaCarrito), BorderLayout.CENTER);
        return p;
    }

    private JPanel vistaTotalPedido() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        p.add(crearTituloSeccion("Consultar Total de Pedido (Función SQL)"), BorderLayout.NORTH);

        JPanel form = crearFormularioResponsivo();
        JTextField txtIdPedido = crearCampoTexto();
        JLabel lblTotal = new JLabel("$0.00");
        lblTotal.setForeground(new Color(56, 189, 248));
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 34));

        JButton btnCalcular = crearBotonAccion("Calcular Total");
        btnCalcular.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtIdPedido.getText().trim());
                BigDecimal total = pedidoService.calcularTotalPedido(id);
                lblTotal.setText("$" + total);
                notificar("Total calculado desde PostgreSQL para el Pedido #" + id, false);
            } catch (NumberFormatException ex) {
                notificar("Ingresa un ID de pedido válido.", true);
            } catch (SQLException ex) {
                manejarErrorSQL(ex);
            }
        });

        agregarFilaForm(form, 0, "ID del Pedido:", txtIdPedido);
        agregarFilaForm(form, 1, "", btnCalcular);
        agregarFilaForm(form, 2, "Total calculado:", lblTotal);

        p.add(form, BorderLayout.NORTH);
        return p;
    }

    private JPanel vistaCambiarEstado() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        p.add(crearTituloSeccion("Cambiar Estado de Pedido (Procedure)"), BorderLayout.NORTH);

        JPanel form = crearFormularioResponsivo();
        JTextField txtIdPedido = crearCampoTexto();
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{
            "PENDIENTE", "EN_PREPARACION", "LISTO", "ENTREGADO"
        });
        cmbEstado.setEditable(true);

        JButton btnActualizar = crearBotonAccion("Actualizar Estado");
        btnActualizar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtIdPedido.getText().trim());
                String estado = cmbEstado.getSelectedItem().toString().trim().toUpperCase();
                pedidoService.actualizarEstadoPedido(id, estado);
                notificar("Pedido #" + id + " actualizado a '" + estado + "'.", false);
            } catch (NumberFormatException ex) {
                notificar("Ingresa un ID de pedido válido.", true);
            } catch (IllegalArgumentException ex) {
                notificar(ex.getMessage(), true);
            } catch (SQLException ex) {
                manejarErrorSQL(ex);
            }
        });

        agregarFilaForm(form, 0, "ID del Pedido:", txtIdPedido);
        agregarFilaForm(form, 1, "Nuevo Estado:", cmbEstado);
        agregarFilaForm(form, 2, "", btnActualizar);

        p.add(form, BorderLayout.NORTH);
        return p;
    }

    private JPanel vistaHistorial() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);
        p.add(crearTituloSeccion("Historial de Estados (Trigger Auditoría)"), BorderLayout.NORTH);

        JPanel form = crearFormularioResponsivo();
        JTextField txtIdPedido = crearCampoTexto();
        areaHistorial = crearAreaResultado();
        JButton btnVer = crearBotonAccion("Consultar Historial");

        btnVer.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtIdPedido.getText().trim());
                List<String> historial = pedidoService.obtenerHistorial(id);
                if (historial.isEmpty()) {
                    areaHistorial.setText("No hay registros de historial para el Pedido #" + id);
                } else {
                    areaHistorial.setText(String.join("\n", historial));
                }
                notificar("Historial consultado para Pedido #" + id, false);
            } catch (NumberFormatException ex) {
                notificar("Ingresa un ID de pedido válido.", true);
            } catch (SQLException ex) {
                manejarErrorSQL(ex);
            }
        });

        agregarFilaForm(form, 0, "ID del Pedido:", txtIdPedido);
        agregarFilaForm(form, 1, "", btnVer);

        p.add(form, BorderLayout.NORTH);
        p.add(envolverScrollGlass(areaHistorial), BorderLayout.CENTER);
        return p;
    }

    // =========================================================================
    // CARGA DE DATOS Y MANEJO DE ERRORES
    // =========================================================================

    private void cargarTablaProductos(boolean soloDisponibles) {
        try {
            modeloProductos.setRowCount(0);
            List<Producto> lista = soloDisponibles ? productoDAO.obtenerDisponibles() : productoDAO.obtenerTodos();
            for (Producto prod : lista) {
                modeloProductos.addRow(new Object[]{
                    prod.getIdProducto(), prod.getNombre(),
                    "$" + prod.getPrecio(), prod.isDisponible() ? "Sí" : "No"
                });
            }
            notificar("Productos cargados: " + lista.size(), false);
        } catch (SQLException e) {
            manejarErrorSQL(e);
        }
    }

    private void cargarTablaPedidos() {
        try {
            modeloPedidos.setRowCount(0);
            List<Pedido> lista = pedidoService.listarPedidos();
            for (Pedido ped : lista) {
                modeloPedidos.addRow(new Object[]{
                    ped.getIdPedido(), ped.getCliente(), ped.getFecha(), ped.getEstado()
                });
            }
            notificar("Pedidos cargados: " + lista.size(), false);
        } catch (SQLException e) {
            manejarErrorSQL(e);
        }
    }

    private void manejarErrorSQL(SQLException e) {
        String state = e.getSQLState();
        String msgUsuario;
        if ("23505".equals(state)) {
            msgUsuario = "Dato duplicado: el registro ya existe en la base de datos.";
        } else if ("23514".equals(state)) {
            msgUsuario = "Operación revertida (ROLLBACK): un valor incumple las reglas permitidas.";
        } else if ("23503".equals(state)) {
            msgUsuario = "Referencia inválida: el cliente o producto no existe.";
        } else if ("P0001".equals(state)) {
            msgUsuario = "Regla de negocio rechazada: " + e.getMessage();
        } else {
            msgUsuario = "Error de base de datos (" + state + "): " + e.getMessage();
        }
        notificar(msgUsuario, true);
        JOptionPane.showMessageDialog(this, msgUsuario, "Aviso del Sistema", JOptionPane.WARNING_MESSAGE);
    }

    private void notificar(String mensaje, boolean esError) {
        lblEstadoGlobal.setText(mensaje);
        lblEstadoGlobal.setForeground(esError ? new Color(251, 113, 133) : new Color(52, 211, 153));
    }

    // =========================================================================
    // HELPERS DE DISEÑO RESPONSIVO
    // =========================================================================

    private JLabel crearTituloSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        return lbl;
    }

    private JPanel crearFormularioResponsivo() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        return form;
    }

    private void agregarFilaForm(JPanel form, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 10);
        gbc.gridy = fila;

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(new Color(203, 213, 225));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(campo, gbc);
    }

    private JTextField crearCampoTexto() {
        JTextField txt = new JTextField();
        txt.setBackground(new Color(30, 41, 59));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 65), 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return txt;
    }

    private JButton crearBotonAccion(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(new Color(56, 189, 248));
        btn.setForeground(new Color(15, 23, 42));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        return btn;
    }

    private JTextArea crearAreaResultado() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(new Color(226, 232, 240));
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        return area;
    }

    private JTable estilizarTabla(JTable tabla) {
        tabla.setOpaque(false);
        tabla.setBackground(new Color(15, 23, 42, 140));
        tabla.setForeground(new Color(241, 245, 249));
        tabla.setGridColor(new Color(255, 255, 255, 30));
        tabla.setRowHeight(28);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.getTableHeader().setBackground(new Color(30, 41, 59));
        tabla.getTableHeader().setForeground(new Color(56, 189, 248));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setOpaque(false);
        cellRenderer.setForeground(new Color(241, 245, 249));
        tabla.setDefaultRenderer(Object.class, cellRenderer);
        return tabla;
    }

    private JScrollPane envolverScrollGlass(Component comp) {
        JScrollPane scroll = new JScrollPane(comp);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 45), 1, true));
        return scroll;
    }
}