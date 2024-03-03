package io.github.optijava.core

import io.github.optijava.config.Config
import kotlinx.serialization.Serializable

@Serializable
class BackendMetaData(var config: Config, var freeSpace: Double, var fileCount: Int)