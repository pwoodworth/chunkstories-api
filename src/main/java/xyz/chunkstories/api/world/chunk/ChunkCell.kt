//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.api.world.chunk

import xyz.chunkstories.api.world.WorldCell
import xyz.chunkstories.api.world.cell.CellComponents

interface ChunkCell : WorldCell {
    val chunk: Chunk

    @get:Deprecated("")
            /** Accesses the raw data in that getCell. Reserved for internal engine
             * purposes!  */
    val data: Int

    val components: CellComponents

    fun refreshRepresentation()
}
