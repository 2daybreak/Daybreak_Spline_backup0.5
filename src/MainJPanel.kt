import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelListener
import java.awt.event.MouseWheelEvent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.JScrollPane
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel

class MainJPanel: JPanel() {

    // Mouse, Key, Logic
    var ing = 0
    var u = Vector3()
    var flag = true
    var mode = Mode.View
    enum class Mode{View, Curve}

    // Geometry
    var bspline = mutableListOf<Bspline>()

    // Table
    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    val subFrame = SubFrame(JScrollPane(table))

    // Viewport
    //var viewport = Viewport()
    val size = 20

    init {
        background = Color(30, 30, 30)
        addMouseListener(object: MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                when (e.clickCount) {
                    2 -> println("DoubleClicked")
                    3 -> println("TripleClicked")
                    4 -> println("QuadrupleClicked")
                }
            }
            override fun mouseExited (e: MouseEvent) { }
            override fun mouseEntered(e: MouseEvent) { }
            override fun mousePressed(e: MouseEvent) {
                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        val v = Vector3(e.x, e.y, 0)
                        u = v
                        when(mode) {
                            Mode.View -> {}
                            Mode.Curve -> {
                                for (p in bspline[ing].pts) {
                                    if (Point3(size, p).contains(e.x, e.y)) {
                                        flag = false
                                        u = p
                                        break
                                    }
                                }
                                if (flag) bspline[ing].addPts(v)
                                flag = true
                            }
                        }

                    }
                    MouseEvent.BUTTON2 -> {}
                    MouseEvent.BUTTON3 -> {}
                }
            }
            override fun mouseReleased(e: MouseEvent) { repaint() }
        })
        addMouseMotionListener(object: MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
                when(mode) {
                    Mode.View -> {}
                    Mode.Curve -> {
                        val i = bspline[ing].pts.indexOf(u)
                        bspline[ing].removePts(u)
                        val v = Vector3(e.x, e.y, 0)
                        when (i) {
                            -1 -> bspline[ing].addPts(v)
                            else -> bspline[ing].addPts(i, v)
                        }
                        u = v
                        repaint()
                    }
                }

            }
            override fun mouseMoved(e: MouseEvent) { }
        })
        addMouseWheelListener(object: MouseWheelListener {
            override fun mouseWheelMoved(e: MouseWheelEvent?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
        iniTable()
    }

    private fun iniTable() {
        table.background = Color(50, 50, 50)
        table.foreground = Color.lightGray
        tableModel.addTableModelListener { e: TableModelEvent ->
            val row = e.firstRow
            val column = e.column
            if (row > -1) {
                try {
                    val x = table.getValueAt(row, 0).toString().toDouble()
                    val y = table.getValueAt(row, 1).toString().toDouble()
                    val z = table.getValueAt(row, 2).toString().toDouble()
                    bspline[ing].removePts(row)
                    bspline[ing].addPts(row, Vector3(x, y, z))
                } catch (e: NumberFormatException) {
                    println(e.message)
                } finally {
                    repaint()
                }

            }
        }
    }

    override fun paintComponent(g: Graphics) {
        updatePaint(g)
        updateTable()
    }

    private fun updatePaint(g: Graphics) {

        super.paintComponent(g)
        g as Graphics2D
        val size = 10.0
        val half = size / 2
        for (b in bspline) {

            //Draw control polygon
            g.color = Color.GRAY; g.stroke = BasicStroke()
            for (i in 1 until b.ctrlPts.size) {
                g.drawLine( b.ctrlPts[i - 1].x.toInt(),
                        b.ctrlPts[i - 1].y.toInt(),
                        b.ctrlPts[i].x.toInt(),
                        b.ctrlPts[i].y.toInt())
            }

            //Draw B-spline curve (interpolation of pts)
            g.color = Color.CYAN; g.stroke = BasicStroke()
            if(b == bspline[ing]) g.color = Color.YELLOW
            val n = b.pts.size * 8
            if(b.pts.size > 1) for (i in 1 until n) {
                val p1 = b.curvePoint((i - 1).toDouble() / (n - 1).toDouble())
                val p2 = b.curvePoint(      i.toDouble() / (n - 1).toDouble())
                g.drawLine(p1.x.toInt(),
                        p1.y.toInt(),
                        p2.x.toInt(),
                        p2.y.toInt())
            }

            //Draw ctrlPts
            g.color = Color.LIGHT_GRAY; g.stroke = BasicStroke()
            for (v in b.ctrlPts) g.draw(Ellipse2D.Double(v.x - half, v.y - half, size, size))

            //Draw pts
            g.color = Color.YELLOW; g.stroke = BasicStroke()
            for(v in b.pts) g.draw(Ellipse2D.Double(v.x - half, v.y - half, size, size))        }
    }

    private fun updateTable() {
        when(mode) {
            Mode.View -> {}
            Mode.Curve -> {
                val list = mutableListOf<Array<Double>>()
                for(p in bspline[ing].pts)
                    list.add(arrayOf(p.x, p.y, p.z))
                val data = list.toTypedArray()
                val head = arrayOf("x", "y", "z")
                tableModel.setDataVector(data,head)
            }
        }
    }

}