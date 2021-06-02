.onLoad <- function(libname, pkgname) {
  writeLines("Calling on load")
  writeLines(dir(file.path(getwd(), "inst/java"), full.names = TRUE))
  writeLines("lib name is")
  writeLines(libname)
  writeLines("pkgname is")
  writeLines(pkgname)
  rJava::.jinit()
  rJava::.jpackage(pkgname, lib.loc = libname)
  rJava::.jaddClassPath(paste(libname,"/",pkgname,"/","java"))
  writeLines(rJava::.jclassPath())
}