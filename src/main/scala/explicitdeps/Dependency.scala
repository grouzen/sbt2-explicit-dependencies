/*
 * Copyright 2018-2023 Chris Birchall
 * Copyright 2026 Michael Nedokushev
 * Licensed under the Apache License, Version 2.0.
 * Modified for this independent sbt port by Michael Nedokushev, 2026.
 */
package explicitdeps

final case class Dependency(organization: String, name: String, version: String, crossVersion: Boolean) {
  override def toString: String =
    if (crossVersion) s""""$organization" %% "$name" % "$version""""
    else s""""$organization" % "$name" % "$version""""
}
