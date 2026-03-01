package xyz.lbres.badselectorsv2

import xyz.lbres.badselectorsv2.abstracts.BaseFragment
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.badselectorsv2.devtools.DeveloperToolsDialog

/**
 * Configuration for dev build flavor
 */
object ProductFlavor : ProductFlavorConfig {
    override val devMode = true

    /**
     * Show dev tools button and setup dialog
     */
    override fun setupFlavor(activity: BaseActivity) {
        val devToolsButton = activity.binding.devToolsButton
        devToolsButton.visible()

        val dialog = DeveloperToolsDialog()
        devToolsButton.setOnClickListener {
            val fragmentManager = BaseFragment.dialogFragmentManager

            if (fragmentManager != null) {
                dialog.show(fragmentManager, DeveloperToolsDialog.TAG)
            }
        }
    }
}
