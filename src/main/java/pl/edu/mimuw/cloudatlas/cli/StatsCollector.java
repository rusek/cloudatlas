package pl.edu.mimuw.cloudatlas.cli;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.zones.Attribute;

public class StatsCollector {
	
	private String exec(String cmd) throws IOException {
		Process process = Runtime.getRuntime().exec(
				new String[]{"bash", "-c", cmd},
				new String[]{"LANG=C"}
		);
		String stdout = IOUtils.toString(process.getInputStream(), "UTF-8");
		String stderr = IOUtils.toString(process.getErrorStream(), "UTF-8");
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw new InterruptedIOException();
		}
		int exitStatus = process.exitValue();
		if (exitStatus != 0) {
			throw new IOException("Command failed: " + stderr);
		}
		
		return StringUtils.trim(stdout);
	}
	
	private long execLong(String cmd) throws IOException {
		return Long.parseLong(exec(cmd));
	}
	
	private long extractMeminfo(String name) throws IOException {
		return execLong("grep '\\b" + name + ":' /proc/meminfo | awk '{print $2}'") * 1024;
	}

	public DoubleValue getAvgCPULoad() throws IOException {
		String[] loads = exec("uptime  | awk -F 'load average:' '{ print $2}'").split(",\\s+");
		return new DoubleValue(Double.parseDouble(loads[0]));
	}

	public IntegerValue getFreeDiskSpace() {
		long result = 0;
		
		for (File root : File.listRoots()) {
			result += root.getFreeSpace();
		}
		
		return new IntegerValue(result);
	}

	public IntegerValue getTotalDiskSpace() {
		long result = 0;
		
		for (File root : File.listRoots()) {
			result += root.getTotalSpace();
		}
		
		return new IntegerValue(result);
	}

	public IntegerValue getFreeRAM() throws IOException {
		return new IntegerValue(extractMeminfo("MemFree") + extractMeminfo("Buffers") + extractMeminfo("Cached"));
	}

	public IntegerValue getTotalRAM() throws IOException {
		return new IntegerValue(extractMeminfo("MemTotal"));
	}

	public IntegerValue getFreeSwap() throws IOException {
		return new IntegerValue(extractMeminfo("SwapFree"));
	}

	public IntegerValue getTotalSwap() throws IOException {
		return new IntegerValue(extractMeminfo("SwapTotal"));
	}

	public IntegerValue getNumProcesses() throws IOException {
		return new IntegerValue(execLong("ps ax | wc -l"));
	}

	public IntegerValue getNumCores() throws IOException {
		return new IntegerValue(execLong("grep -c ^processor /proc/cpuinfo"));
	}

	public StringValue getKernelVersion() throws IOException {
		return new StringValue(exec("uname -v"));
	}

	public IntegerValue getNumLoggedUsers() throws IOException {
		return new IntegerValue(execLong("who | awk '{print $1}' | sort -u | wc -l"));
	}

	public SetValue<StringValue> getDNSNames() throws IOException {
		SetValue<StringValue> result = SetValue.of(SimpleType.STRING);
		
		for (String dns : exec("grep nameserver /etc/resolv.conf | awk '{print $2}'").split("\\s+")) {
			result.addItem(new StringValue(dns));
			if (result.size() >= 3) {
				break;
			}
		}
		
		return result;
	}
	
	public List<Attribute> collectStats() throws IOException {
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("cpu_load", getAvgCPULoad()));
		attributes.add(new Attribute("free_disk", getFreeDiskSpace()));
		attributes.add(new Attribute("total_disk", getTotalDiskSpace()));
		attributes.add(new Attribute("free_ram", getFreeRAM()));
		attributes.add(new Attribute("total_ram", getTotalRAM()));
		attributes.add(new Attribute("free_swap", getFreeSwap()));
		attributes.add(new Attribute("total_swap", getTotalSwap()));
		attributes.add(new Attribute("num_processes", getNumProcesses()));
		attributes.add(new Attribute("num_cores", getNumCores()));
		attributes.add(new Attribute("kernel_ver", getKernelVersion()));
		attributes.add(new Attribute("logged_users", getNumLoggedUsers()));
		attributes.add(new Attribute("dns_names", getDNSNames()));
		return attributes;
	}
}
