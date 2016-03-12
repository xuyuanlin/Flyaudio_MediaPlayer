package com.flyaudio.flyMediaPlayer.until;

public class FFT {

	private double[] xConv;// 对x[n]进行二进制倒序排列的结果

	public FFT(double[] fftBuffer) throws Exception {
		int n, m, j;
		n = fftBuffer.length;
		m = 0;
		j = 1;

		m = (int) (Math.log(n) / Math.log(2));

		xConv = fftBuffer;

		i2Sort(xConv, m); // 将xConv进行二进制倒序排序
		// System.out.println("x[n]共有"+fftBuffer.length+'('+m+"阶)"+"个采样值！"+"补零个数为："+(j-n)+'\n');

		myFFT(xConv, m);
	}

	/*
	 * * 方法名：i2Sort 功能：二进制倒序排序，在进行基2-FFT蝶形算法前，可以用到此算法。 参数： doubl[]
	 * xConv2;需要排序的数组 int m;xConv2中元素所占2进制位宽 返回值：void
	 */
	private void i2Sort(double[] xConv2, int m) {
		int[] index = new int[xConv2.length]; // index数组用于，倒序索引
		int[] bits = new int[m];
		double[] temp = new double[xConv2.length];

		for (int i = 0; i < xConv2.length; i++)
			// xConv2的原序映像
			temp[i] = xConv2[i];

		for (int i = 0; i < index.length; i++) {
			index[i] = i; // 第i个位置，倒序前的值为i
			for (int j = 0; j < m; j++) {
				bits[j] = index[i] - index[i] / 2 * 2; // 提取index[i]的第j位二进制的值
				index[i] /= 2;
			}
			index[i] = 0; // 清零第i个位置的值
			for (int j = m, power = 1; j > 0; j--) {
				index[i] += bits[j - 1] * power; // 第i个位置，倒序后的位置
				power *= 2;
			}
			// System.out.println(index[i]); //倒序效果预览
		}

		for (int i = 0; i < xConv2.length; i++)
			// 倒序实现
			xConv2[i] = temp[index[i]];
	}

	/*
	 * 方法名：myFFT 功能：FFT算法 参数： double[] xConv2; 补零并进行二进制倒序后的x[n] int m; m =
	 * log2(n),n为采样个数 返回值：void
	 */
	private void myFFT(double[] xConv2, int m) {
		int divBy; // divBy等分
		double[] Xr, Xi, Wr, Wi; // 分别表示：FFT结果的实部和虚部、旋转因子的实部和虚部
		double[] tempXr, tempXi; // 蝶形结果暂存器
		int n = xConv2.length;
		double pi = Math.PI;
		divBy = 1;
		Xr = new double[n];
		Xi = new double[n];
		tempXr = new double[n];
		tempXi = new double[n];
		Wr = new double[n / 2];
		Wi = new double[n / 2];

		// System.out.println("经过二进制倒序排列后的x[n]:");
		for (int i = 0; i < n; i++) { // 初始化Xr、Xi，之所以这样初始化，是为了方便下面的蝶形结果暂存
			Xr[i] = xConv2[i];
			Xi[i] = 0;
			// System.out.println(String.format("%6.2f", xConv2[i]));
		}

		for (int i = 0; i < m; i++) { // 共需要进行m次蝶形计算
			divBy *= 2;
			for (int k = 0; k < divBy / 2; k++) { // 旋转因子赋值
				Wr[k] = Math.cos(k * 2 * pi / divBy);
				Wi[k] = -Math.sin(k * 2 * pi / divBy);
			}

			for (int j = 0; j < n; j++) { // 蝶形结果暂存
				tempXr[j] = Xr[j];
				tempXi[j] = Xi[j];
			}

			for (int k = 0; k < n / divBy; k++) { // 蝶形运算：每一轮蝶形运算，都有n/2对的蝴蝶参与；n/2分为n/divBy组，每组divBy/2个。
				int wIndex = 0; // 旋转因子下标索引
				for (int j = k * divBy; j < k * divBy + divBy / 2; j++) {
					double X1 = tempXr[j + divBy / 2] * Wr[wIndex]
							- tempXi[j + divBy / 2] * Wi[wIndex];
					double X2 = tempXi[j + divBy / 2] * Wr[wIndex]
							+ tempXr[j + divBy / 2] * Wi[wIndex];
					Xr[j] = tempXr[j] + X1;
					Xi[j] = tempXi[j] + X2;
					Xr[j + divBy / 2] = tempXr[j] - X1; // 蝶形对两成员距离相差divBy/2
					Xi[j + divBy / 2] = tempXi[j] - X2;
					wIndex++;
				}
			}
		}

		for (int i = 0; i < n; i++) // FFT结果显示
		{
			xConv2[i] = Math.hypot(Xr[i], Xi[i]) / (n / 2);
		}
	}

}
