//import lazy.LazyGui
import MathUtils.pi
import com.krab.lazy.LazyGui
import processing.core.PApplet
import processing.core.PVector
import java.text.DecimalFormat


fun main(args: Array<String>) {
    PlottedXYZ.run()
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
            0
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

private fun PApplet.draw_curve_2d(
    range:Int = 500,
    fn: (x: Float)->PVector,
    amp: Float = 1.0f,
    offs: Float = 0.0f
){
    beginShape()
    for(x in 0..range){
        val idx = x.toFloat()/range.toFloat()
        var sx = fn(idx)
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

class PlottedXYZ : PApplet() {
    companion object {
        fun run() {
            val art = PlottedXYZ()
            art.runSketch()
        }
    }
    public lateinit var gui: LazyGui

    override fun settings() {
        size(800,800,P3D)
        smooth(8)
    }

    override fun setup() {
        this.gui = LazyGui(this)

        windowResizable(true)
    }

    override fun draw() {
        val standardObserver1931 = floatArrayOf(
                0.001368F, 0.000039F, 0.006450F, // 380 nm
                0.002236F, 0.000064F, 0.010550F, // 385 nm
                0.004243F, 0.000120F, 0.020050F, // 390 nm
                0.007650F, 0.000217F, 0.036210F, // 395 nm
                0.014310F, 0.000396F, 0.067850F, // 400 nm
                0.023190F, 0.000640F, 0.110200F, // 405 nm
                0.043510F, 0.001210F, 0.207400F, // 410 nm
                0.077630F, 0.002180F, 0.371300F, // 415 nm
                0.134380F, 0.004000F, 0.645600F, // 420 nm
                0.214770F, 0.007300F, 1.039050F, // 425 nm
                0.283900F, 0.011600F, 1.385600F, // 430 nm
                0.328500F, 0.016840F, 1.622960F, // 435 nm
                0.348280F, 0.023000F, 1.747060F, // 440 nm
                0.348060F, 0.029800F, 1.782600F, // 445 nm
                0.336200F, 0.038000F, 1.772110F, // 450 nm
                0.318700F, 0.048000F, 1.744100F, // 455 nm
                0.290800F, 0.060000F, 1.669200F, // 460 nm
                0.251100F, 0.073900F, 1.528100F, // 465 nm
                0.195360F, 0.090980F, 1.287640F, // 470 nm
                0.142100F, 0.112600F, 1.041900F, // 475 nm
                0.095640F, 0.139020F, 0.812950F, // 480 nm
                0.057950F, 0.169300F, 0.616200F, // 485 nm
                0.032010F, 0.208020F, 0.465180F, // 490 nm
                0.014700F, 0.258600F, 0.353300F, // 495 nm
                0.004900F, 0.323000F, 0.272000F, // 500 nm
                0.002400F, 0.407300F, 0.212300F, // 505 nm
                0.009300F, 0.503000F, 0.158200F, // 510 nm
                0.029100F, 0.608200F, 0.111700F, // 515 nm
                0.063270F, 0.710000F, 0.078250F, // 520 nm
                0.109600F, 0.793200F, 0.057250F, // 525 nm
                0.165500F, 0.862000F, 0.042160F, // 530 nm
                0.225750F, 0.914850F, 0.029840F, // 535 nm
                0.290400F, 0.954000F, 0.020300F, // 540 nm
                0.359700F, 0.980300F, 0.013400F, // 545 nm
                0.433450F, 0.994950F, 0.008750F, // 550 nm
                0.512050F, 1.000000F, 0.005750F, // 555 nm
                0.594500F, 0.995000F, 0.003900F, // 560 nm
                0.678400F, 0.978600F, 0.002750F, // 565 nm
                0.762100F, 0.952000F, 0.002100F, // 570 nm
                0.842500F, 0.915400F, 0.001800F, // 575 nm
                0.916300F, 0.870000F, 0.001650F, // 580 nm
                0.978600F, 0.816300F, 0.001400F, // 585 nm
                1.026300F, 0.757000F, 0.001100F, // 590 nm
                1.056700F, 0.694900F, 0.001000F, // 595 nm
                1.062200F, 0.631000F, 0.000800F, // 600 nm
                1.045600F, 0.566800F, 0.000600F, // 605 nm
                1.002600F, 0.503000F, 0.000340F, // 610 nm
                0.938400F, 0.441200F, 0.000240F, // 615 nm
                0.854450F, 0.381000F, 0.000190F, // 620 nm
                0.751400F, 0.321000F, 0.000100F, // 625 nm
                0.642400F, 0.265000F, 0.000050F, // 630 nm
                0.541900F, 0.217000F, 0.000030F, // 635 nm
                0.447900F, 0.175000F, 0.000020F, // 640 nm
                0.360800F, 0.138200F, 0.000010F, // 645 nm
                0.283500F, 0.107000F, 0.000000F, // 650 nm
                0.218700F, 0.081600F, 0.000000F, // 655 nm
                0.164900F, 0.061000F, 0.000000F, // 660 nm
                0.121200F, 0.044580F, 0.000000F, // 665 nm
                0.087400F, 0.032000F, 0.000000F, // 670 nm
                0.063600F, 0.023200F, 0.000000F, // 675 nm
                0.046770F, 0.017000F, 0.000000F, // 680 nm
                0.032900F, 0.011920F, 0.000000F, // 685 nm
                0.022700F, 0.008210F, 0.000000F, // 690 nm
                0.015840F, 0.005723F, 0.000000F, // 695 nm
                0.011359F, 0.004102F, 0.000000F, // 700 nm
                0.008111F, 0.002929F, 0.000000F, // 705 nm
                0.005790F, 0.002091F, 0.000000F, // 710 nm
                0.004109F, 0.001484F, 0.000000F, // 715 nm
                0.002899F, 0.001047F, 0.000000F, // 720 nm
                0.002049F, 0.000740F, 0.000000F, // 725 nm
                0.001440F, 0.000520F, 0.000000F, // 730 nm
                0.001000F, 0.000361F, 0.000000F, // 735 nm
                0.000690F, 0.000249F, 0.000000F, // 740 nm
                0.000476F, 0.000172F, 0.000000F, // 745 nm
                0.000332F, 0.000120F, 0.000000F, // 750 nm
                0.000235F, 0.000085F, 0.000000F, // 755 nm
                0.000166F, 0.000060F, 0.000000F, // 760 nm
                0.000117F, 0.000042F, 0.000000F, // 765 nm
                0.000083F, 0.000030F, 0.000000F, // 770 nm
                0.000059F, 0.000021F, 0.000000F, // 775 nm
                0.000042F, 0.000015F, 0.000000F   // 780 nm
            );

        val T = millis()/1000.0f
        val fov = PI/3.0f;
        val cameraZ = (height/2.0f) / tan(fov/2.0f);
        perspective(fov, width.toFloat()/height.toFloat(),
            gui.slider("zmin"), gui.slider("zmax"));

        pushMatrix();
            scale(width/2.0f,width/2.0f,width/2.0f)
//        scale(width/2.0f,width/2.0f,1.0f)
            translate(1.0f,1.0f,1.0f);
            camera( gui.plotXYZ("eyePos"), gui.plotXYZ("lookAtPos") );

            val grad = gui.gradient("grad")
            background(gui.colorPicker("bg").hex);
            noFill()
            w_strokeWeight(5.0f)
            colorMode(HSB, 1.0f)


        hint(ENABLE_STROKE_PERSPECTIVE)




        stroke(gui.colorPicker("line col").hex)

//        box(gui.slider("boxsz"));

//        color(255)
        line(Vec(0,0,4),Vec(0,0,-4))
        line(Vec(-4,0,0),Vec(4,0,0))
        line(Vec(0,-4,0),Vec(0,4,0))




        push()
//            tint(gui.slider("grid opacity"))
            w_strokeWeight(2.0f)
            val line_iters = 20

            val alpha = gui.slider("alpha")
            for(i in 0..line_iters){
                val idx = i.toFloat()/line_iters * 8.0f - 4.0f

//                    (0xFFFFFF & col + 0x01000000 * alpha)
                stroke(gui.colorPicker("line col x").adjust(alpha = alpha))
//                tint(255f, 0.0f)
                line(Vec(4,idx,0),Vec(-4,idx,0))
                line(Vec(-4,0, idx),Vec(4,0,idx))
                stroke(gui.colorPicker("line col y").adjust(alpha = alpha))
                line(Vec(idx,0,4),Vec(idx,0,-4))
                line(Vec(0,idx,4),Vec(0,idx,-4))
                stroke(gui.colorPicker("line col z").adjust(alpha = alpha))
                line(Vec(idx,-4,0),Vec(idx,4,0))
                line(Vec(0,-4, idx),Vec(0,4,idx))


            }
        pop()




        // -- GRID
//        val shader = ShaderReloader.getShader("grid.frag", "gridVert.glsl")
//        shader["time"] = T
//        ShaderReloader.shader("grid.frag", "gridVert.glsl")

        val loop_cnt = standardObserver1931.size/3
        push()
        stroke(gui.colorPicker("pt col ").hex)
        w_strokeWeight(10.0f)
        var cdf = ArrayList<Float>(loop_cnt)
        var integral = 0f;

        for (i in 0 until loop_cnt){
            val samp = standardObserver1931[i*3+1]
            integral += samp
            cdf.add(integral)
//            cdf[i] = integral

            val uv_x = i.toFloat()/loop_cnt - 0.5f
            point(uv_x,-samp,0.01f)
        }

        line(
            Vec(-1,-1,0),
            Vec(1,-1,0),
        )
        stroke(gui.colorPicker("pt col integral").hex)

        var idx = 0
        draw_curve(
            amp = 1.0f,
            offs = 0.0f,
            range = loop_cnt - 1,
            fn = { t:Float->
                var t = t

                val samp = cdf[idx]/integral
                idx += 1
                -samp
            }
        )


        val print_btn_pressed = gui.button("print")
        idx = 0
        draw_curve_2d(
            amp = 1.0f,
            offs = 0.0f,
            range = loop_cnt - 1,
            fn = { t:Float->
                var t = t


                val samp_p = Vec(t,cdf[idx]/integral)
                val norm = Vec(cos(-pi/4), sin(-pi/4))
                val samp_p_reflected = samp_p - (norm*2f*(samp_p.x*norm.x + samp_p.y * norm.y))

                if(print_btn_pressed){

                    val df = DecimalFormat("#")
                    df.maximumFractionDigits = 8

                    kotlin.io.print("(" + df.format(samp_p_reflected.x)  + "," + df.format(samp_p_reflected.y) + "),")
                }

                samp_p_reflected.y *= -1;

                idx += 1
                samp_p_reflected
            }
        )


//        for(i in 0..200){
//            var r = random(1.0f);
//        }

//        for (i in 0 until loop_cnt){
//            val samp = cdf[i]/integral
//
//            val uv_x = i.toFloat()/loop_cnt - 0.5f
//            point(uv_x,-samp,0.01f)
//        }
        pop()

        // -- plot

        w_strokeWeight(14.0f)

        for (i in 0 until 3){
            var idx = 0
            stroke(gui.colorPicker("curve col " + i.toString()).hex)
            draw_curve(
                amp = 1.0f,
                offs = 0.0f,
                range = standardObserver1931.size/3-3,
                fn = { t:Float->
                    var t = t


                    val s = standardObserver1931[idx*3 + i]
                    idx += 1
                    -s
                }
            )
        }
//        standardObserver1931

//        noStroke()
//        fill(1)
//        shader(shader)

//        square(0f,0f,20f)

//        resetShader();



//        line(Vec(0,0.03,-4),Vec(0,-0.2,4))



//        fun x(t: Float): Float {
////            return sin(t*gui.slider("freq"))
//            val t = t * freq
//            return fract(t)
//            val mt = fract(t)
//            if(mt < 0.5)
//                return -1f
//            else
//                return 1f
////            return sin(t*)
//        }


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
//
//        val sampleRate = gui.slider("sample rate",0f,0f,44100f)




        push()
//        {
//
//        }
        pop()




        popMatrix();
    }
}

