USE [RM_Db]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Ban](
	[MaBan] [varchar](10) NOT NULL,
	[SoGhe] [int] NULL,
	[GhiChu] [nvarchar](200) NULL,
 CONSTRAINT [PK_Ban] PRIMARY KEY CLUSTERED 
(
	[MaBan] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChiTietDonHang](
	[MaChiTietDonHang] [varchar](10) NOT NULL,
	[MaDonHang] [varchar](10) NOT NULL,
	[MaMon] [varchar](10) NOT NULL,
	[SoLuong] [int] NULL,
 CONSTRAINT [PK_ChiTietDonHang] PRIMARY KEY CLUSTERED 
(
	[MaChiTietDonHang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DatBan](
	[MaDatBan] [varchar](10) NOT NULL,
	[MaBan] [varchar](10) NOT NULL,
	[TenKhachDaiDien] [nvarchar](50) NULL,
	[SoKhach] [int] NOT NULL,
	[SDTKhach] [nvarchar](20) NOT NULL,
	[Email] [varchar](50) NULL,
	[ThoiGianHen] [date] NOT NULL,
	[TrangThai] [nvarchar](50) NULL,
	[GhiChu] [nvarchar](200) NULL,
 CONSTRAINT [PK_Table_1] PRIMARY KEY CLUSTERED 
(
	[MaDatBan] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DonHang](
	[MaDonHang] [varchar](10) NOT NULL,
	[MaDatBan] [varchar](10) NOT NULL,
	[MaNV] [varchar](10) NOT NULL,
	[ThoiGianTao] [date] NULL,
	[ThoiGianThanhToan] [date] NULL,
 CONSTRAINT [PK_DonHang] PRIMARY KEY CLUSTERED 
(
	[MaDonHang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DungCu](
	[MaDungCu] [varchar](10) NOT NULL,
	[TenDungCu] [nvarchar](50) NULL,
	[Loai] [nvarchar](50) NULL,
	[SoLuong] [int] NULL,
	[TinhTrang] [nvarchar](50) NULL,
	[NgayThongKe] [date] NOT NULL,
 CONSTRAINT [PK_DungCu] PRIMARY KEY CLUSTERED 
(
	[MaDungCu] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[MonAn](
	[MaMon] [varchar](10) NOT NULL,
	[TenMon] [nvarchar](50) NULL,
	[HinhAnh] [image] NULL,
	[Gia] [money] NULL,
	[VAT] [float] NULL,
	[DonVi] [nvarchar](50) NULL,
	[Nhom] [nvarchar](50) NULL,
	[MonAnKem] [nvarchar](50) NULL,
	[SanSang] [bit] NOT NULL,
 CONSTRAINT [PK_MonAn] PRIMARY KEY CLUSTERED 
(
	[MaMon] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[com.restaurant.restaurant_management_project.model.NhanVien](
	[MaNV] [varchar](10) NOT NULL,
	[TenNV] [nvarchar](50) NOT NULL,
	[NgaySinh] [date] NULL,
	[SDT] [varchar](15) NOT NULL,
	[Email] [varchar](255) NULL,
	[ChucVu] [nvarchar](50) NOT NULL,
	[Luong] [money] NULL,
 CONSTRAINT [PK_NhanVien] PRIMARY KEY CLUSTERED 
(
	[MaNV] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TaiKhoan](
	[MaNV] [varchar](10) NOT NULL,
	[TenTK] [varchar](50) NOT NULL,
	[MatKhau] [varchar](50) NOT NULL,
 CONSTRAINT [PK_TaiKhoan_1] PRIMARY KEY CLUSTERED 
(
	[MaNV] ASC,
	[TenTK] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Ban] ADD  CONSTRAINT [DF_Ban_SoGhe]  DEFAULT ((0)) FOR [SoGhe]
GO
ALTER TABLE [dbo].[DatBan] ADD  CONSTRAINT [DF_DatBan_ThoiGianHen]  DEFAULT (getdate()) FOR [ThoiGianHen]
GO
ALTER TABLE [dbo].[DonHang] ADD  CONSTRAINT [DF_DonHang_ThoiGianTao]  DEFAULT (getdate()) FOR [ThoiGianTao]
GO
ALTER TABLE [dbo].[DonHang] ADD  CONSTRAINT [DF_DonHang_ThoiGianLap]  DEFAULT (getdate()) FOR [ThoiGianThanhToan]
GO
ALTER TABLE [dbo].[DungCu] ADD  CONSTRAINT [DF__DungCu__NgayThon__5070F446]  DEFAULT (getdate()) FOR [NgayThongKe]
GO
ALTER TABLE [dbo].[MonAn] ADD  CONSTRAINT [DF_MonAn_SanSang]  DEFAULT ((1)) FOR [SanSang]
GO
ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD  CONSTRAINT [FK_ChiTietDonHang_DonHang] FOREIGN KEY([MaDonHang])
REFERENCES [dbo].[DonHang] ([MaDonHang])
GO
ALTER TABLE [dbo].[ChiTietDonHang] CHECK CONSTRAINT [FK_ChiTietDonHang_DonHang]
GO
ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD  CONSTRAINT [FK_ChiTietDonHang_MonAn] FOREIGN KEY([MaMon])
REFERENCES [dbo].[MonAn] ([MaMon])
GO
ALTER TABLE [dbo].[ChiTietDonHang] CHECK CONSTRAINT [FK_ChiTietDonHang_MonAn]
GO
ALTER TABLE [dbo].[DatBan]  WITH CHECK ADD  CONSTRAINT [FK_DatBan_Ban] FOREIGN KEY([MaBan])
REFERENCES [dbo].[Ban] ([MaBan])
GO
ALTER TABLE [dbo].[DatBan] CHECK CONSTRAINT [FK_DatBan_Ban]
GO
ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD  CONSTRAINT [FK_DonHang_DatBan] FOREIGN KEY([MaDatBan])
REFERENCES [dbo].[DatBan] ([MaDatBan])
GO
ALTER TABLE [dbo].[DonHang] CHECK CONSTRAINT [FK_DonHang_DatBan]
GO
ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD  CONSTRAINT [FK_DonHang_NhanVien] FOREIGN KEY([MaNV])
REFERENCES [dbo].[com.restaurant.restaurant_management_project.model.NhanVien] ([MaNV])
GO
ALTER TABLE [dbo].[DonHang] CHECK CONSTRAINT [FK_DonHang_NhanVien]
GO
ALTER TABLE [dbo].[MonAn]  WITH CHECK ADD  CONSTRAINT [FK_MonAn_MonAn] FOREIGN KEY([MaMon])
REFERENCES [dbo].[MonAn] ([MaMon])
GO
ALTER TABLE [dbo].[MonAn] CHECK CONSTRAINT [FK_MonAn_MonAn]
GO
ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD  CONSTRAINT [FK_TaiKhoan_NhanVien] FOREIGN KEY([MaNV])
REFERENCES [dbo].[com.restaurant.restaurant_management_project.model.NhanVien] ([MaNV])
GO
ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [FK_TaiKhoan_NhanVien]
GO
