.onLoad <- function(libname, pkgname) {
  writeLines("Calling on load")
  writeLines(dir(file.path(getwd(), "inst/java")))
  writeLines("lib name is")
  writeLines(libname)
  writeLines("pkgname is")
  writeLines(pkgname)
  rJava::.jpackage(pkgname, lib.loc = libname)
  rJava::.jaddClassPath(dir(file.path(getwd(), "inst/java"), full.names = TRUE))
}