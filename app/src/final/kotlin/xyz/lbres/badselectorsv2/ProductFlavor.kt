package xyz.lbres.badselectorsv2

/**
 * Configuration for final build flavor
 */
object ProductFlavor : ProductFlavorConfig {
    override val devMode = false
    override fun setupFlavor(activity: BaseActivity) {}
}
