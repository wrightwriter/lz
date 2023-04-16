//import lazy.LazyGui
import MathUtils.pi
import com.krab.lazy.LazyGui
import com.krab.lazy.stores.GlobalReferences
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PConstants.RIGHT
import processing.core.PVector
import processing.event.MouseEvent
import kotlin.math.max
import kotlin.math.min


fun main(args: Array<String>) {
    PlotterBSDF.run()
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
//        sx.x -= 0.5f;
        sx *= amp
        sx.y += offs

        if(x== 0)
            curveVertex(sx)
        if(x == range)
            curveVertex(sx)

        curveVertex(sx)
    }
    endShape()
}


enum class CamMode {
    PILOT,
    ORBIT
}
class WCamera(
    var cam_mode: CamMode = CamMode.ORBIT
) {

    var theta = 0.5f;
    var phi = 0.4f;
    var zoom = -2f;

    var pos = Vec(0,0,-1);
    var target = Vec(0,0,0);

    var smoothing = 0.1f;

    var pos_internal = Vec(0,0,-1);
    var target_internal = Vec(0,0,0);

    var theta_internal = 0f;
    var phi_internal = 0f;
    var zoom_internal = -2f;

    var mouse_was_pressed = false;

    var mouse_hide_pos = Vec(0);

    var cam_mode_prev: CamMode
    var mouse_pos_prev = Vec(0);

    var mouse_pressed = false

    var scroll_wheel = 0f

    init{
        cam_mode_prev = cam_mode
    }
    fun tick(){
        if (cam_mode_prev != cam_mode)
            target = Vec(0,0,0)
        cam_mode_prev = cam_mode

        mouse_pressed = GlobalReferences.app.mousePressed && (GlobalReferences.app.mouseButton == RIGHT)

        val mw = GlobalReferences.app.mouseWheel()

        val new_mouse_pos = Vec(
            GlobalReferences.app.mouseX,
            GlobalReferences.app.mouseY
        )

//        GlobalReferences.app.noCursor()

        if(mouse_pressed != mouse_was_pressed){
            if(mouse_pressed){
                mouse_hide_pos = new_mouse_pos
                mouse_pos_prev = mouse_hide_pos.copy()
                GlobalReferences.app.noCursor()
            } else {
                GlobalReferences.appWindow.warpPointer(mouse_hide_pos.x.toInt(), mouse_hide_pos.y.toInt());
                GlobalReferences.app.cursor()
            }
            mouse_was_pressed = mouse_pressed
        }

        if(mouse_pressed){
            var mouse_delta = new_mouse_pos - mouse_pos_prev
            mouse_delta /= GlobalReferences.app.width

            val mouse_speed = 1f;
            theta -= mouse_delta.x*mouse_speed
            phi -= mouse_delta.y*mouse_speed


            mouse_pos_prev = mouse_hide_pos.copy()
            GlobalReferences.appWindow.warpPointer(mouse_hide_pos.x.toInt(), mouse_hide_pos.y.toInt());
        }

        zoom -= scroll_wheel*0.15f
        scroll_wheel = 0f


        phi = min(phi,1f)
        phi = max(phi,0f)

        theta_internal = lerp(theta_internal, theta, smoothing)
        phi_internal = lerp(
            phi_internal,
            if(cam_mode == CamMode.PILOT) 1f-phi else phi,
            smoothing
        )
        zoom_internal = lerp(zoom_internal, zoom, smoothing)

        val pc = Vec(
            PApplet.sin(theta_internal * pi * 2f) * PApplet.sin(phi_internal*pi),
            PApplet.cos(phi_internal*pi),
            PApplet.cos(theta_internal * pi * 2f) * PApplet.sin(phi_internal*pi)
        ) * zoom_internal

        if(cam_mode == CamMode.ORBIT){
            target_internal = lerp(target_internal, target, smoothing)
            pos_internal = pc
        } else {
            var move_vec = Vec(0,0,0);
            val key = GlobalReferences.app.key
            if (GlobalReferences.app.keyPressed) {
                if (key == 'w') {
                    move_vec.z += 1
                } else if (key == 's'){
                    move_vec.z -= 1
                }
                if (key == 'a') {
                    move_vec.x += 1
                } else if (key == 'd'){
                    move_vec.x -= 1
                }
            }
            val right_vec = Vec(0,1,0).cross(pc)

            pos += (right_vec*move_vec.x + pc*move_vec.z)/GlobalReferences.app.frameRate
            pos_internal = lerp(pos_internal, pos, smoothing)
            target_internal = pos_internal + pc
        }
    }

    fun set_polar(_theta: Float = 0f, _phi: Float = 0f, _zoom: Float = 0f){
        theta = _theta
        phi = _phi
        zoom = _zoom
    }

    fun set_target(_target: PVector){
        target = _target
    }

    fun set_pos(_pos: PVector){
        pos = _pos
    }

}

class PlotterBSDF : PApplet() {
    companion object {
        fun run() {
            val art = PlotterBSDF()
            art.runSketch()
        }
    }
    public lateinit var gui: LazyGui
    public val cam = WCamera(CamMode.ORBIT)

    override fun settings() {
        size(800,800,P3D)
        smooth(8)
    }

    override fun setup() {
        frameRate(120f)
        this.gui = LazyGui(this)

        windowResizable(true)
    }
    override fun mouseWheel(event: MouseEvent) {
        val e: Float = event.getCount().toFloat()
        cam.scroll_wheel = e
    }
//    override fun keyPressed() {
//        if (key.code == PConstants.) {
//            if (keyCode == UP) {
////                fillVal = 255
//            } else if (keyCode == DOWN) {
////                fillVal = 0
//            }
//        } else {
////            fillVal = 126
//        }
//    }

    override fun draw() {
        val T = millis()/1000.0f
        val fov = PI/3.0f;
        val cameraZ = (height/2.0f) / tan(fov/2.0f);
        perspective(fov, width.toFloat()/height.toFloat(),
            gui.slider("zmin"), gui.slider("zmax"));

        pushMatrix();
            scale(width/2.0f,width/2.0f,width/2.0f)
//        scale(width/2.0f,width/2.0f,1.0f)
            translate(1.0f,1.0f,1.0f);


//            val eye = gui.plotXYZ("eyePos")
//            cam.set_polar(eye.x, eye.y, eye.z)

            if (gui.button("pilot-fly toggle"))
                cam.cam_mode = if(cam.cam_mode==CamMode.PILOT) CamMode.ORBIT else CamMode.PILOT
            cam.tick()
            camera( cam.pos_internal, cam.target_internal );


            val grad = gui.gradient("grad")
            background(gui.colorPicker("bg").hex);
            noFill()
            w_strokeWeight(5.0f)
            colorMode(HSB, 1.0f)

        scale(1f,-1f,1f)


        hint(ENABLE_STROKE_PERSPECTIVE)




        stroke(gui.colorPicker("line col").hex)

        box(gui.slider("boxsz"));
        line(Vec(0,0,4),Vec(0,0,-4))
        line(Vec(-4,0,0),Vec(4,0,0))
        line(Vec(0,-4,0),Vec(0,4,0))


        push()
            w_strokeWeight(2.0f)
            val line_iters = 20

            val alpha = gui.slider("alpha")
            for(i in 0..line_iters){
                val idx = i.toFloat()/line_iters * 8.0f - 4.0f

                stroke(gui.colorPicker("line col x").adjust(alpha = alpha))
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


        val theta = 0.6.toFloat()
        val phi = 0.8.toFloat()

        val cnt = 10;



        stroke(gui.colorPicker("polar col").hex)
        for (th in 0..cnt){
            draw_curve_2d(
                amp = 1.0f,
                offs = 0.0f,
                range = 10,
                fn = { t:Float->
                    var phi = t*pi/2f

                    val pc = Vec(
                        sin(th.toFloat()/cnt*pi*2f) * sin(phi),
                        cos(phi),
                        cos(th.toFloat()/cnt*pi*2f) * sin(phi)
                    )
                    pc
                }
            )

        }

        randomSeed(1251250);




        fun ggx(alpha: Float, theta: Float): Float{
            val cos_theta = cos(theta)
            val nom = alpha*alpha
            var den = ((alpha*alpha - 1f)*cos_theta*cos_theta + 1)
            den *= den
            den *= pi

            return nom/den
        }


        val N_mode = gui.radio("N mode", arrayOf("ggx", "blinn", "beckmann"))


        val ggx_alpha = gui.slider("ggx alpha")


        val sampling_mode = gui.radio("sample mode", arrayOf("sqrt", "no sqrt", "normal offs", "acos", "cosine weighted", "pow cosine"))

        fun uniform_pdf_theta(theta: Float): Float{
            return sin(theta)
        }

        fun cosine_pdf_theta(theta: Float): Float{
            return 2f*sin(theta)*cos(theta)
        }
        fun cosine_pdf_phi(): Float{
            return 1f/(2f*pi)
        }

        push()
            for (i in 0..gui.sliderInt("hemi iters",20,0,30000)){
                val col = gui.gradientColorAt("grad",random(1.0f))
                w_strokeWeight(10f)
                stroke(col.hex)


                val phi = random(1.0f)
                var th = random(1.0f)
                if(sampling_mode.equals("sqrt") || sampling_mode.equals("normal offs")){
                    th = pi/2f-sqrt(1f-th*th)*pi/2f
                } else if (sampling_mode.equals("no sqrt")){
                    th *= pi/2f
                } else if (sampling_mode.equals("acos")  ){
                    th = acos(th)
                } else if (sampling_mode.equals("cosine weighted")){
                    th = acos(sqrt(th))
                } else if (sampling_mode.equals("pow cosine")){
                    th = acos(pow(th,1f/(ggx_alpha + 1)))
                }


                var pc = Vec(
                    sin(phi*pi*2f) * sin(th),
                    cos(th),
                    cos(phi*pi*2f) * sin(th)
                )
                if(sampling_mode.equals("normal offs")){
                    pc = (pc + Vec(0,1,0)).normalize()
                } else if (sampling_mode.equals("acos")){
//                    pc /= uniform_pdf_theta(th)

                } else if (sampling_mode.equals("cosine weighted")){
//                    pc *= cosine_pdf_theta(th)
//                    pc *= cosine_pdf_phi()
                    pc /= cos(th)
                }


                if(N_mode.equals("ggx")){
                    val _ggx = ggx(ggx_alpha, th)
                    pc *= _ggx
                }




                point(pc)
                w_strokeWeight(2f)
                stroke(col.adjust(alpha = -0.6f))
                line(Vec(0),pc)
            }
        pop()


        popMatrix();
    }
}

