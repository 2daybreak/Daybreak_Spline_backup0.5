//Using light weight component (e.g. Swing)
import java.awt.*
import java.awt.event.*
import java.awt.geom.Ellipse2D
import javax.swing.*

class MainFrame : JFrame() {
    init {

        title = "Daybreak"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true

        val contentPane: Container = getContentPane()
        contentPane.layout = GridLayout(1, 2)
        contentPane.add(MainJPanel())
        contentPane.add(MainJTable())
        contentPane.addKeyListener(KeyHandler())
        contentPane.requestFocus()

        setSize(400, 300)
    }
}

class MainJTable : JTable(10,2) {

    init {

        background = Color(30,30,30)

    }
}

class MainJPanel : JPanel() {

    var xi: Int = 0
    var yi: Int = 0
    var bspline = mutableListOf<Bspline>()
    var flag = true
    //private List<Nurbs> nurbs = new List<Nurbs>();
    //var viewport = Viewport()

    init {
        background = Color(30, 30, 30)
        bspline.add(Bspline(3))
        val size = 10.toDouble()
        val half = size / 2
        addMouseListener(
                object : MouseListener {
                    override fun mouseClicked(e: MouseEvent) {
                        when(e.clickCount) {
                            1 -> {}//println("MouseClicked")
                            2 -> println("DoubleClicked")
                            3 -> println("TripleClicked")
                            4 -> println("QuadrupleClicked")
                        }
                    }
                    override fun mouseEntered(e: MouseEvent) {}

                    override fun mouseExited(e: MouseEvent) { background = Color.BLACK }

                    override fun mousePressed(e: MouseEvent) {
                        when(e.button) {
                            MouseEvent.BUTTON1 -> {
                                var r = Vector3()
                                for(p in bspline[0].pts) if(Point3(p).contains(e.point.x,e.point.y)) {
                                    flag = false
                                    r= p
                                    println("pts removed")
                                }
                                bspline[0].removePts(r)
                                if(flag) {
                                    bspline[bspline.size - 1].addPts(Vector3(e.x.toDouble(), e.y.toDouble(), 0.toDouble()))
                                    println("pts added")
                                }
                                flag = true
                            }
                            MouseEvent.BUTTON2 -> {}
                            MouseEvent.BUTTON3 -> {}
                        }
                    }

                    override fun mouseReleased(e: MouseEvent) {
                        repaint()
                    }
                })
        addMouseMotionListener(
                object : MouseMotionListener {
                    override fun mouseDragged(e: MouseEvent) {}

                    override fun mouseMoved(e: MouseEvent) {}
                })
        addMouseWheelListener {
            //viewport.zoom += e.wheelRotation
        }
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        val size = 10.0
        val half = size / 2
        for (b in bspline) {
            //println("degree=${b.degree}")
            //println("order=${b.order}")
            //println("knots=${b.knots}")

            g.color = Color.YELLOW //Color of Pts
            g2.color = Color.YELLOW //Color of Pts
            for(v in b.pts) g2.draw(Ellipse2D.Double(v.x - half, v.y - half, size, size))
            g2.color = Color.LIGHT_GRAY //Color of ctrlPts
            for (v in b.ctrlPts) g2.draw(Ellipse2D.Double(v.x - half, v.y - half, size, size))
            g.color = Color.GRAY //Color of control polygon
            for (i in 1 until b.ctrlPts.size) {
                g.drawLine(b.ctrlPts[i - 1].x.toInt(),
                        b.ctrlPts[i - 1].y.toInt(),
                        b.ctrlPts[i].x.toInt(),
                        b.ctrlPts[i].y.toInt())
            }
            g.color = Color.CYAN //Color of interpolation
            val n = 20
            if(b.pts.size > 1) for (i in 1 until n) {
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

class KeyHandler : KeyListener {
    override fun keyPressed(e: KeyEvent) {
        println("keyCode=${e.keyCode}")
        println("keyChar=${e.keyChar}")
        println("keyText=${KeyEvent.getKeyText(e.keyCode)}")
    }
    override fun keyReleased(e: KeyEvent) {}
    override fun keyTyped(e: KeyEvent) {}
}