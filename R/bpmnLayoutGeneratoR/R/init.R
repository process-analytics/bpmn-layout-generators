.onLoad <- function(libname, pkgname) {
  rJava::.jinit()
  rJava::.jpackage(pkgname, lib.loc = libname)
  rJava::.jaddClassPath(paste(libname, "/", pkgname, "/", "java"))
  writeLines("Using classpath")
  writeLines(rJava::.jclassPath())
}