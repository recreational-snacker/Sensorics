package com.aconno.acnsensa.domain.interactor.type

import io.reactivex.Flowable

/**
 * @author aconno
 */
interface FlowableUseCaseWithParameter<T, in P> {
    fun execute(parameter: P): Flowable<T>
}
