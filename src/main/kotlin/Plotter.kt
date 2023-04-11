import MathUtils.tau
import com.krab.lazy.LazyGui
//import lazy.LazyGui
import processing.core.PApplet
import processing.sound.AudioSample
import processing.sound.SoundFile



fun main(args: Array<String>) {
    Plotter.run()
}

private fun PApplet.playAudio(
    fn: (t: Float)->Float,
    dur: Float = 1.0f,
){
    val sampleRate = 44100
    val samples = FloatArray(sampleRate.toInt())
    for (i in 0 until sampleRate.toInt()) {
//                samples[i] = sin(400.0f*tau * i / sampleRate)
        samples[i] = fn(i.toFloat()/sampleRate)
    }
    val sample = AudioSample(this,samples,1*sampleRate.toInt())
    sample.amp(0.2f)
    sample.playFor(dur)
    Thread.sleep(1000)
    sample.stop()

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

class Plotter : PApplet() {
    companion object {
        fun run() {
            val art = Plotter()
            art.runSketch()
        }
    }
    public lateinit var gui: LazyGui

    override fun settings() {
        size(800,800,P3D)
        smooth(8)
    }

//    public lateinit var audioBuff: AudioSample
//    var sample: AudioSample? = null

    override fun setup() {
        this.gui = LazyGui(this)

//        val samples = FloatArray(44100.toInt())
//
//        for (i in 0 until 44100.toInt()) {
//            samples[i] = sin(400.0f*tau * i / 44100)
//        }
//        val sample = AudioSample(this,samples,1)
//        sample.amp(0.2f)

//        this.audioBuff = AudioSample(this,samples,1)
        // Create an array and manually write a single sine wave oscillation into it.
        // Create an array and manually write a single sine wave oscillation into it.

        // Create the audiosample based on the data, set framerate to play 200 oscillations/second

        // Create the audiosample based on the data, set framerate to play 200 oscillations/second


        windowResizable(true)
    }

    override fun draw() {
        pushMatrix();
        scale(width/2.0f,height/2.0f)
        translate(1.0f,1.0f);

        background(gui.colorPicker("bg").hex);
//        scale(gui.slider("sz"))

        noFill()

        w_strokeWeight(120.0f)

        colorMode(HSB, 1.0f)


        val freq = gui.slider("freq")
        fun x(t: Float): Float {
//            return sin(t*gui.slider("freq"))
            val t = t * freq
            return fract(t)
            val mt = fract(t)
            if(mt < 0.5)
                return -1f
            else
                return 1f
//            return sin(t*)
        }


        curveTightness(gui.slider("curve tightness"))
        push()
            w_strokeWeight(10.0f)
            stroke(gui.colorPicker("fn color input").hex)
            draw_curve(amp = 0.2f, offs = 0.4f, fn =
                { _x:Float->
                    val a = 3
                    x(_x)
                }
            )
        pop()

        val sampleRate = gui.slider("sample rate",0f,0f,44100f)

        fun polyMBLEP(phase: Float, phase_step: Float): Float{
            var t = phase
            if (t < phase_step) {
                t /= phase_step;
                return t+t - t*t - 1f;
            }
            else if (t > 1f - phase_step) {
                t = (t - 1f) / phase_step;
                return t*t + t+t + 1f;
            }
            else return 0f;
        }

        fun xAntialiased(t: Float):Float{
            val phase = fract(t*freq)
            val phase_step = (freq) / sampleRate

            val mblep = polyMBLEP(phase, phase_step)

            val s = x(t) - mblep
            return s
        }

        push()
            w_strokeWeight(10.0f)
            stroke(gui.colorPicker("fn color output").hex)

            draw_curve(amp = 0.2f, offs = -0.2f, fn =
                { t:Float->
                    var t = t

                    val s = xAntialiased(t)
                    s
                }
            )
        pop()


        if(gui.button("play audio")){
            playAudio(fn = { t: Float->
                x(t)
            })
        }

        if(gui.button("play audio antialiased")){
            playAudio(fn = { t: Float->
                xAntialiased(t)
            })
        }

        popMatrix();
    }
}

