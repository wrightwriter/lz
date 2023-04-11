import com.krab.lazy.LazyGui
import com.krab.lazy.ShaderReloader
import processing.core.PApplet
import processing.core.PVector



fun main(args: Array<String>) {
    Flame.run()
}


private fun PApplet.draw_curve(
    range:Int = 500,
    fn: (x: Float)->Float,
    amp: Float = 1.0f,
    offs: Float = 0.0f
){
    beginShape()
    for(x in 0..range){
        val idx = x.toFloat()/range.toFloat()
        var sx = Vec(
            idx,
//                    sin(idx*tau*freq.toFloat() + 0.25f*tau),
            fn(idx),
            1
        )
        sx.x -= 0.5f;
        sx.y *= amp
        sx.y += offs

        if(x== 0)
            curveVertex(sx)
        if(x == range)
            curveVertex(sx)

        curveVertex(sx)
    }
    endShape()
}


data class FlameData(val name: String = "", val age: Int = 0, var running: Boolean = true, var offs: Float = 0.0f)

class Flame : PApplet() {
    companion object {
        fun run() {
            val art = Flame()
            art.runSketch()
        }
    }
    public lateinit var gui: LazyGui
    lateinit var floatArray : Array<FloatArray>
    val threads = mutableListOf<Thread>() // declare a list of threads
    val numThreads = 4 // set the number of threads

    val flameData = FlameData()

    var fr = 1

    var cam = Vec(0)


    override fun settings() {
        size(800,800,P3D)
        smooth(8)
    }

    override fun setup() {
        this.gui = LazyGui(this)

        floatArray = Array(width) { FloatArray(height) } // initialize the 2D float array

        val one_over_w_h = Vec(1)/Vec(width,height)
        for (i in 0 until numThreads) {
            val thread = Thread {
                while (true){
                    var particles: Array<PVector> = Array(1000 ){Vec(0)}

                    if(flameData.running){
                        val time = millis() / 1000.0f
                        var k = 0
                        val range = 100000
                        val one_over_range = 1f/range.toFloat()
                        for (particle in particles){

                            val randomX = (0 until range).random().toFloat()*one_over_range
                            val randomY = (0 until range).random().toFloat()*one_over_range
                            val randomZ = (0 until range).random().toFloat()*one_over_range

//                            particle.x = 1f

//                            var vv: PVector = particles[k]
                            var p = particles[k]

                            if(randomZ > 0.7f){
                                p = sin(p *5f + 200f)
                            } else if(randomZ > 0.3f){
//                                p = (p - 0.2 + flameData.offs).abs()/p.dot(p)*5f
                                p = (p - 0.2 + flameData.offs).abs()/p.dot(p)
                            } else {
                                p = p.rotate(-0.5f)
                                p -= Vec(0.4,0.1)
                            }

                            particles[k] = p.copy()

//                            p.x += 0.5f
//                            p.y += 0.5f

                            p += cam

                            p *= 0.4f

                            p.x *= width
                            p.y *= height


//                            p.x += randomX
//                            p.y += randomZ
//                            p.z += randomX


//                            particle = Vec(1,1,1)
                            if(
                                p.x > 0 &&
                                p.y > 0 &&
                                p.y < height &&
                                p.x < width
                                ){
//                                floatArray[p.x.toInt()][p.y.toInt()] += randomZ.toFloat()/255.0f*0.005f
                                floatArray[p.x.toInt()][p.y.toInt()] += 0.02f
                            }
                            k++
                        }
                    } else {
                        Thread.sleep(10)

                    }
                }
            }
            threads.add(thread)
        }

        // start the threads

        threads.forEach { it.start() }
//        threads.forEach { it.join() }


        windowResizable(true)
    }
    fun updateGlslFilter() {
        val time = gui.slider("time")
        val timeSpeed = gui.slider("time ++", radians(1f))
        gui.sliderSet("time", time + timeSpeed)


        cam = gui.plotXY("cam", cam)


        // change something in data/template.glsl
        //  and then save the myShader.glsl file in any text editor
        //  to see it re-compiled and displayed in real-time
        val shaderPath = "template.glsl"
        val shader = ShaderReloader.getShader(shaderPath)
        shader["time"] = time
        ShaderReloader.filter(shaderPath)
    }

    override fun draw() {
        pushMatrix();
        scale(width/2.0f,height/2.0f)
        translate(1.0f,1.0f);

        background(gui.colorPicker("bg").hex);

        noFill()
        w_strokeWeight(120.0f)
        colorMode(HSB, 1.0f)

        var offs = gui.slider("offs")
        flameData.offs = offs


        if(gui.button("reset")){
            fr = 0
            for (x in 0 until width) {
                for (y in 0 until height) {
                    floatArray[x][y] = 0f
                }
            }
        }
        if(!flameData.running){
        } else {
            fr++
        }

        if(gui.button("enable")){
            flameData.running = !flameData.running
        }

        val rectSize = width



        loadPixels() // load the pixel buffer for updating
        // nested for loop to iterate through the float array
        val fc_val  = 1.0f/fr.toFloat()
        for (x in 0 until width) {
            for (y in 0 until height) {
                var value = floatArray[x][y] // get the value at (x, y)
                value = value * gui.slider("brightness")*fc_val
//                value = sin( value)*0.5f + 0.5f

                val pixelColor = color(value) // convert the value to a grayscale color
                val pixelIndex = x + y * width // calculate the index of the pixel in the pixel buffer
                pixels[pixelIndex] = pixelColor // update the color of the pixel in the pixel buffer
            }
        }

        updatePixels() // update the screen with the new pixel colors

        updateGlslFilter();



//        curveTightness(gui.slider("curve tightness"))
//        push()
//            w_strokeWeight(10.0f)
//            stroke(gui.colorPicker("fn color input").hex)
//            draw_curve(amp = 0.2f, offs = 0.4f, fn =
//                { _x:Float->
//                    val a = 3
//                    x(_x)
//                }
//            )
//        pop()



        popMatrix();
    }
}

