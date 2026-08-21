/*
 * Copyright 2018-2023 Chris Birchall
 * Copyright 2026 Michael Nedokushev
 * Licensed under the Apache License, Version 2.0.
 * Modified for this independent sbt port by Michael Nedokushev, 2026.
 */
package explicitdeps

import sbt.FeedbackProvidedException

object UndeclaredCompileDependenciesException extends FeedbackProvidedException {
  override def toString = "Failing the build because undeclared dependencies were found"
}

object UnusedCompileDependenciesException extends FeedbackProvidedException {
  override def toString = "Failing the build because unused dependencies were found"
}
