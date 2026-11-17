package net.aoba.rendering.msaa;

import java.util.OptionalInt;
import java.util.OptionalDouble;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

public class SSAAHandler implements IAAHandler {

	private int scale = 1;
	private int currentWidth;
	private int currentHeight;

	private @Nullable GpuTexture colorTexture;
	private @Nullable GpuTextureView colorTextureView;
	private @Nullable GpuSampler sampler;

	private static int sampleCountToScale(int samples) {
		return Math.max(1, (int) Math.round(Math.sqrt(Math.max(1, samples))));
	}

	@Override
	public int renderScale() {
		return scale;
	}

	@Override
	public void prepare(int width, int height, int samples) {
		int newScale = sampleCountToScale(samples);
		int sw = width * newScale;
		int sh = height * newScale;
		if (colorTexture != null && currentWidth == sw && currentHeight == sh && scale == newScale)
			return;

		releaseBuffers();
		scale = newScale;

		colorTexture = RenderSystem.getDevice().createTexture("aoba_ssaa_color",
				GpuTexture.USAGE_RENDER_ATTACHMENT | GpuTexture.USAGE_TEXTURE_BINDING, TextureFormat.RGBA8, sw, sh, 1, 1);
		colorTextureView = RenderSystem.getDevice().createTextureView(colorTexture);
		sampler = RenderSystem.getSamplerCache().get(FilterMode.LINEAR);

		currentWidth = sw;
		currentHeight = sh;
	}

	@Override
	public RenderPass beginPass() {
		return RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Aoba SSAA",
				colorTextureView, OptionalInt.of(0), null, OptionalDouble.empty());
	}

	@Override
	public ResolvedTarget resolve() {
		return new ResolvedTarget(colorTextureView, sampler);
	}

	private void releaseBuffers() {
		if (colorTextureView != null) {
			colorTextureView.close();
			colorTextureView = null;
		}
		if (colorTexture != null) {
			colorTexture.close();
			colorTexture = null;
		}
		sampler = null;
		currentWidth = 0;
		currentHeight = 0;
	}

	@Override
	public void close() {
		releaseBuffers();
	}
}
