package io.github.optijava.core

import kotlinx.serialization.Serializable

@Serializable
open class MetaData(var fileName: String, var id: String = "", var uploadTime: String, var size: Int /* unit: MB */)
