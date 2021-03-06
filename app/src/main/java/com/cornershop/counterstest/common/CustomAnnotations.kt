package com.cornershop.counterstest.common

import javax.inject.Qualifier

/**
 * Annotations for specific purposes.
 */
object CustomAnnotations {

    /**
     * Annotation to difference the dispatchers.
     */
    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class MainDispatcher

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class IODispatcher

    /**
     * Forked from https://github.com/googlesamples/android-architecture-components/blob/d81da2cb1e3d61e40f052e631bb15883d0f9f637/GithubBrowserSample/app/src/debug/java/com/android/example/github/testing/OpenForTesting.kt
     *
     * This annotation allows us to open some classes for mocking purposes while they are final in
     * release builds.
     */
    @Target(AnnotationTarget.ANNOTATION_CLASS)
    annotation class OpenClass

    /**
     * Annotate a class with [OpenForTesting] if you want it to be extendable in debug builds.
     */
    @OpenClass
    @Target(AnnotationTarget.CLASS)
    annotation class OpenForTesting
}