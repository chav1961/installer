package chav1961.installer.internal;

import java.awt.Image;
import java.net.URI;

import javax.swing.Icon;

import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.model.ImageKeeperImpl;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.installer.internal.ProjectInfo/i18n.xml")
@LocaleResource(value="devel.projectInfo",tooltip="devel.projectInfo.tt",help="devel.projectInfo.help")
public class ProjectInfo implements FormManager<Long, ProjectInfo>, ModuleAccessor {
	private final LoggerFacade	logger;
	
	@LocaleResource(value="devel.projectInfo.name",tooltip="devel.projectInfo.name.tt")
	@Format("30ms")
	public String			name = "";
	
	@LocaleResource(value="devel.projectInfo.description",tooltip="devel.projectInfo.description.tt")
	@Format(value="30*5ms",wizardType="")
	public String			description = "";
	
	@LocaleResource(value="devel.projectInfo.version",tooltip="devel.projectInfo.version.tt")
	@Format("30ms")
	public DottedVersion	version = new DottedVersion();

	@LocaleResource(value="devel.projectInfo.vendor",tooltip="devel.projectInfo.vendor.tt")
	@Format("30ms")
	public String			vendor = "";

	@LocaleResource(value="devel.projectInfo.site",tooltip="devel.projectInfo.site.tt")
	@Format("30ms")
	public URI				site = URI.create("./");

	@LocaleResource(value="devel.projectInfo.icon",tooltip="devel.projectInfo.icon.tt")
	@Format("24*24")
	public ImageKeeperImpl	icon = new ImageKeeperImpl();

	@LocaleResource(value="devel.projectInfo.avatar",tooltip="devel.projectInfo.avatar.tt")
	@Format("64*64")
	public ImageKeeperImpl	avatar = new ImageKeeperImpl();
	
	public ProjectInfo(final LoggerFacade logger) {
		this.logger = logger;
	}
	
	@Override
	public RefreshMode onField(final ProjectInfo inst, final Long id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	@Override
	public void allowUnnamedModuleAccess(final Module... unnamedModules) {
		for (Module item : unnamedModules) {
			this.getClass().getModule().addExports(this.getClass().getPackageName(),item);
		}
	}
}
