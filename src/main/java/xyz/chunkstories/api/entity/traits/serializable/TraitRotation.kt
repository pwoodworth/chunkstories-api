//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.api.entity.traits.serializable

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

import org.joml.Vector2f
import org.joml.Vector3d
import org.joml.Vector3dc

import xyz.chunkstories.api.entity.Entity
import xyz.chunkstories.api.world.WorldMaster
import xyz.chunkstories.api.world.serialization.StreamSource
import xyz.chunkstories.api.world.serialization.StreamTarget

class TraitRotation(entity: Entity) : TraitSerializable(entity) {
    var horizontalRotation = 0f
        private set
    var verticalRotation = 0f
        private set

    private val rotationImpulse = Vector2f()

    /** @return A vector3d for the direction
     */
    val directionLookingAt: Vector3dc
        get() {
            val direction = Vector3d()

            val horizontalRotationRad = (horizontalRotation / 360f).toDouble() * 2.0 * Math.PI
            val verticalRotationRad = (verticalRotation / 360f).toDouble() * 2.0 * Math.PI
            return eulerXYtoVec3(direction, horizontalRotationRad, verticalRotationRad)
        }

    val upDirection: Vector3dc
        get() {
            val direction = Vector3d(0.0, 1.0, 0.0)

            val horizontalRotationRad = (horizontalRotation / 360f).toDouble() * 2.0 * Math.PI
            val verticalRotationRad = ((verticalRotation + 90f) / 360f).toDouble() * 2.0 * Math.PI
            return eulerXYtoVec3(direction, horizontalRotationRad, verticalRotationRad)
        }

    private fun eulerXYtoVec3(direction: Vector3d, horizontalRotationRad: Double, verticalRotationRad: Double): Vector3dc {
        direction.x = Math.sin(horizontalRotationRad) * Math.cos(verticalRotationRad)
        direction.y = Math.sin(verticalRotationRad)
        direction.z = Math.cos(horizontalRotationRad) * Math.cos(verticalRotationRad)

        return direction.normalize()
    }

    fun setRotation(horizontalAngle: Double, verticalAngle: Double) {
        this.horizontalRotation = (360 + horizontalAngle).toFloat() % 360
        this.verticalRotation = verticalAngle.toFloat()

        if (verticalRotation > 90)
            verticalRotation = 90f
        if (verticalRotation < -90)
            verticalRotation = -90f

        this.pushComponentEveryone()
    }

    fun addRotation(d: Double, e: Double) {
        setRotation(horizontalRotation + d, verticalRotation + e)
    }

    @Throws(IOException::class)
    override fun push(destinator: StreamTarget, dos: DataOutputStream) {
        dos.writeFloat(horizontalRotation)
        dos.writeFloat(verticalRotation)
    }

    @Throws(IOException::class)
    override fun pull(from: StreamSource, dis: DataInputStream) {
        horizontalRotation = dis.readFloat()
        verticalRotation = dis.readFloat()

        // Position updates received by the server should be told to everyone but the
        // controller
        if (entity.world is WorldMaster)
            this.pushComponentEveryoneButController()
    }

    /** Sends the view flying about  */
    fun applyInpulse(inpulseHorizontal: Double, inpulseVertical: Double) {
        rotationImpulse.add(Vector2f(inpulseHorizontal.toFloat(), inpulseVertical.toFloat()))
    }

    /** Reduces the acceleration and returns it  */
    fun tickInpulse(): Vector2f {
        rotationImpulse.mul(0.50f)
        if (rotationImpulse.length() < 0.05)
            rotationImpulse.set(0.0f, 0.0f)
        return rotationImpulse
    }

}
