package com.example.bolasalex2024

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

class MyAnimationView(context: Context?) : View(context) {
    val balls: ArrayList<ShapeHolder> = ArrayList() //lista donde se guardaran las bolas generadas
    var animation: AnimatorSet? = null//variable que almacena la animacion de les bolas generadas
    var color = 0


    init { //con este init se inicializa el fondo del layout entre color azul y verde
        // Animate background color
// Note that setting the background color will automatically invalidate the
// view, so that the animated color, and the bouncing balls, get redisplayed on
// every frame of the animation.
        val colorAnim: ValueAnimator = ObjectAnimator.ofInt(
            this,
            "backgroundColor",
            BLUE,
            GREEN
        )//animacion de color de fondo de la pantalla del layout de rojo - aul
        colorAnim.duration = 3000 //tiempo de la animacion
        colorAnim.setEvaluator(ArgbEvaluator()) //para interpolar los valores de color
        colorAnim.repeatCount =
            ValueAnimator.INFINITE //tipo de durada de animacion aqui es infinita
        colorAnim.repeatMode =
            ValueAnimator.REVERSE //se invierte la animacion del color en cada repeticion
        colorAnim.start()//inicio animacion
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { //funcion touch se crean animaciones en el layout del xml
        if (event.action != MotionEvent.ACTION_DOWN && //si no esta presionado
            event.action != MotionEvent.ACTION_MOVE //si no se mueve al presionador
        ) {
            return false //devuelve false para indicar que no hay presion en la pantalla
        } //si es lo controrario
        val newBall = addBall(
            event.x,
            event.y
        )// se crea una instancia de lapelota con las coord de presion en pantalla
        // Bouncing animation with squash and stretch
        val startY = newBall.getY() // Obtiene la posición Y actual de la pelota generada
        val endY = 50f // Define la posición Y final hacia arriba para mover la pelota
        val h = height.toFloat() // Obtiene la altura del área de visualización
        val eventY = event.y // Obtiene la posición Y del evento de presión en la pantalla
        val duration =
            (500 * (eventY / h)).toInt() // Calcula la duración de la animación basada en la posición Y del evento

        val bounceAnim: ValueAnimator = ObjectAnimator.ofFloat(
            newBall,
            "y",
            startY,
            endY
        ) // Crea la animación de rebote hacia arriba
        bounceAnim.duration = duration.toLong() // Establece la duración de la animación
        bounceAnim.interpolator = AccelerateInterpolator() // Aplica un interpolador de aceleración

        val squashAnim1: ValueAnimator =
            ObjectAnimator.ofFloat( //aniamcion de estiramiento de la pelota
                newBall, "x", newBall.getX(), //animacion en la pelota generada en la prop Y
                newBall.getX() - 25f //se resta la mitad del valor de la pelota generada en la prop Y
            )
        squashAnim1.duration = (duration / 4).toLong() //duracion de la aniamcion
        squashAnim1.repeatCount = 1 //solo se ejecutara 1 vez la animacion
        squashAnim1.repeatMode =
            ValueAnimator.REVERSE //cuando termine la animacion volvera haciendo lo mismo pero en sentido contrario
        squashAnim1.interpolator = DecelerateInterpolator()//interpolador de acelerado
        val squashAnim2: ValueAnimator = ObjectAnimator.ofFloat( //animacion de estiramiento 2
            newBall,//en la pelota generada solo
            "width",//en lo ancho de la pelota generada
            newBall.getWidth(),//se obtiene el valor del ancho de la pelota
            newBall.getWidth() + 50//le suma 50 para centrar la pelota
        )
        squashAnim2.duration = (duration / 4).toLong() //tiempo de animacion
        squashAnim2.repeatCount = 1//solo se ejecutara 1 vez la animacion
        squashAnim2.repeatMode =
            ValueAnimator.REVERSE//cuando termine la animacion volvera haciendo lo mismo pero en sentido contrario
        squashAnim2.interpolator = DecelerateInterpolator()//interpolador de acelerado

        // Sequence the down/squash&stretch/up animations
        val bouncer =
            AnimatorSet()//crea una variable de animnationset donde se agruparan las animaciones creadas
        bouncer.play(bounceAnim)
            .before(squashAnim1) //se reproduce la animacion bounceanim antes de la de squash
        bouncer.play(squashAnim1)
            .with(squashAnim2) //se reproducen las 2 animaciones al mismo tiempo


        // Fading animation - remove the ball when the animation is done
        val fadeAnim: ValueAnimator = ObjectAnimator.ofFloat(
            newBall,
            "alpha",
            1f,
            0f
        )//animacion en la pelota generada propiedad transparencia
        fadeAnim.duration = 250 //duracion de la animaciopn

        fadeAnim.addListener(object : AnimatorListenerAdapter() {
            //listener para eliminar la pelota generada
            override fun onAnimationEnd(animation: Animator) {//cuando la animacion termnina
                balls.remove((animation as ObjectAnimator).target) //se elimina la pelota generada
            }
        })

        // Sequence the two animations to play one after the other
        val animatorSet = AnimatorSet()//instancia para secuenciar las animaciones anteriores
        animatorSet.play(bouncer).before(fadeAnim)//se reproduce bouncer antes de fadeanim
        // Start the animation
        animatorSet.start() //empieza la animacion

        return true//devuelve true para indicar que todo salio OK
    }

    companion object {
        //colores visibles para todos
        private const val RED = -0x7f80
        private const val BLUE = -0x7f7f01
        private const val CYAN = -0x7f0001
        private const val GREEN = -0x7f0080
    }

    private fun addBall(x: Float, y: Float): ShapeHolder {
        val circle = OvalShape()
        circle.resize(50f, 50f)
        val drawable = ShapeDrawable(circle)
        val shapeHolder = ShapeHolder(drawable)
        shapeHolder.setX(x - 25f)
        shapeHolder.setY(y - 25f)




        val random =
            (0..1).random() // Generar un número aleatorio entre 0 y 1 para elegir entre azul y rojo
        if (random == 0) { // Si el número aleatorio es 0, el color será rojo
            color = RED
        } else { // Si el número aleatorio es 1, el color será azul
            color = BLUE
        }

        val paint = drawable.paint
        paint.color = color // Establecer el color elegido
        shapeHolder.setPaint(paint)
        balls.add(shapeHolder)
        return shapeHolder
    }

    override fun onDraw(canvas: Canvas) { //funcion ondraw de las pelotas generadas
        Log.d("OnDraw", "OnDraw")//Mensaje por consola
        for (i in balls.indices) { //con un bucle dentro de las pelotas generada
            val shapeHolder =
                balls[i] as ShapeHolder //se obtiene el ShapeHolder de la pelota selecionada
            canvas.save()//se guarda el metodo de canvas
            canvas.translate(shapeHolder.getX(), shapeHolder.getY())//se translada el canvas
            shapeHolder.getShape()!!.draw(canvas) //se dibuja el ShapeHolder la pelota generada
            canvas.restore()//restaura el canvas
        }
    }
}
