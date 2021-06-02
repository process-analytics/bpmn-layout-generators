.onLoad <- function(libname, pkgname) {
  cat("Calling on load")
  rJava::.jpackage(pkgname, lib.loc = libname)
  rJava::.jaddClassPath(dir(file.path(getwd(), "java"), full.names = TRUE))
}