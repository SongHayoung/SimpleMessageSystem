package io.github.sumfi.worker.util

import io.github.sumfi.worker.exception.RandomSamplingException
import java.lang.Exception
import kotlin.random.Random
import kotlin.random.nextInt

object RandomExceptionUtil {
	fun samplingFailure(sample: Int = 100): Boolean {
		check(sample in 0..100) { "sample value must in range [0, 100]" }
		val randomValue = Random.nextInt(IntRange(0, 100))
		if (randomValue > sample) {
			throw RandomSamplingException("random value $randomValue is greater than $sample")
		}
		return Random.nextInt(IntRange(0, 100)) > sample
	}
}