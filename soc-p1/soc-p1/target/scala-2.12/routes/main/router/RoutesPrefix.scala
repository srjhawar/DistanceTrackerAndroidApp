
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Swati/Desktop/SEM3/SOC/soc-p1/conf/routes
// @DATE:Wed Aug 30 23:59:11 GMT-04:00 2017


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
