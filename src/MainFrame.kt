//Using light weight component (e.g. Swing)
import java.awt.*
import java.awt.event.*
import javax.swing.*

class MainFrame : JFrame() {
    init {
        //val panel = MainPanel()

        val contentPane: Container = getContentPane()
        contentPane.layout = GridLayout(1, 1)
        contentPane.add(MainPanel())

        this.title = "Daybreak"
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.setSize(400, 300)
        isVisible = true

        contentPane.addKeyListener(keyHandler())
        contentPane.requestFocus()
    }
}

class MainPanel : JPanel() {

    var pts = mutableListOf<Vector3>()
    var bspline = mutableListOf<Bspline>()
    //private List<Nurbs> nurbs = new List<Nurbs>();

    //var viewport = Viewport()

    init {
        addMouseListener(
                object : MouseListener {
                    override fun mouseClicked(e: MouseEvent) {
                        if (e.clickCount == 1) {
                            println("MouseClicked")
                        } else if (e.clickCount == 2) {
                            println("DoubleClicked")
                        }
                    }

                    override fun mouseEntered(e: MouseEvent) {
                        val p = e.source as JPanel
                        p.background = Color(30, 30, 30)
                    }

                    override fun mouseExited(e: MouseEvent) {
                        val p = e.source as JPanel
                        p.background = Color.BLACK
                    }

                    override fun mousePressed(e: MouseEvent) {
                        if (e.button == MouseEvent.BUTTON1) {
                            pts.add(Vector3(e.x.toDouble(), e.y.toDouble(), 0.toDouble()))
                            bspline[bspline.size - 1].addPts(Vector3(e.x.toDouble(), e.y.toDouble(), 0.toDouble()))
                        } else if (e.button == MouseEvent.BUTTON2) {
                            println("MiddleButton")
                        } else if (e.button == MouseEvent.BUTTON3) {
                            println("RightButton")
                            bspline.add(Bspline(3))
                        }
                    }

                    override fun mouseReleased(e: MouseEvent) {
                        println("MouseReleased" + e.x + "," + e.y)
                    }
                })
        addMouseMotionListener(
                object : MouseMotionListener {
                    override fun mouseDragged(e: MouseEvent) {
                        //System.out.println("MouseDragged" + e.getX() + "," + e.getY());
                    }

                    override fun mouseMoved(e: MouseEvent) {
                        //System.out.println("MouseMoved" + e.getX() + "," + e.getY());
                    }
                })
        addMouseWheelListener { e ->
            //viewport.zoom += e.wheelRotation
        }

    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.color = Color.YELLOW
        for (v in pts) {
            g.fillOval(v.x.toInt() - 5, v.y.toInt() - 5, 10, 10)
        }
        for (b in bspline) {
            println("degree=${b.degree}")
            println("degree=${b.order}")
            println("knots=${b.knots}")
            //Draw control points
            g.color = Color.LIGHT_GRAY
            for (v in b.ctrlPts) {
                g.fillOval(v.x.toInt() - 4, v.y.toInt() - 4, 8, 8)
            }
            //Draw control polygon
            g.color = Color.GRAY
            for (i in 1 until b.ctrlPts.size) {
                g.drawLine(b.ctrlPts[i - 1].x.toInt(),
                        b.ctrlPts[i - 1].y.toInt(),
                        b.ctrlPts[i].x.toInt(),
                        b.ctrlPts[i].y.toInt())
            }
            //Draw interpolated curve
            g.color = Color.CYAN
            val n = 20
            for (i in 1 until n) {
                val p1 = b.curvePoint((i - 1).toDouble() / (n - 1).toDouble())
                val p2 = b.curvePoint(      i.toDouble() / (n - 1).toDouble())
                g.drawLine(p1.x.toInt(),
                           p1.y.toInt(),
                           p2.x.toInt(),
                           p2.y.toInt())
            }

        }
    }
}

class keyHandler : KeyListener {
    override fun keyPressed(e: KeyEvent) {
        val keyCode = e.keyCode
        val keyChar = e.keyChar
        println("keyCode=$keyCode")
        println("keyChar=$keyChar")
        println("keyText=" + KeyEvent.getKeyText(keyCode))
    }

    override fun keyReleased(e: KeyEvent) {}
    override fun keyTyped(e: KeyEvent) {}
}

