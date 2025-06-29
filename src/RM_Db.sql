-- Xóa các SEQUENCE nếu chúng đã tồn tại
IF OBJECT_ID('dbo.Ban_Seq', 'SO') IS NOT NULL DROP SEQUENCE dbo.Ban_Seq;
IF OBJECT_ID('dbo.DBan_Seq', 'SO') IS NOT NULL DROP SEQUENCE dbo.DBan_Seq;
IF OBJECT_ID('dbo.DH_Seq', 'SO') IS NOT NULL DROP SEQUENCE dbo.DH_Seq;
IF OBJECT_ID('dbo.DC_Seq', 'SO') IS NOT NULL DROP SEQUENCE dbo.DC_Seq;
IF OBJECT_ID('dbo.MA_Seq', 'SO') IS NOT NULL DROP SEQUENCE dbo.MA_Seq;
IF OBJECT_ID('dbo.NV_Seq', 'SO') IS NOT NULL DROP SEQUENCE dbo.NV_Seq;


-- Tạo SEQUENCE cho từng bảng để sinh mã tự động
CREATE SEQUENCE dbo.Ban_Seq
    AS INT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    NO CYCLE;

CREATE SEQUENCE dbo.DBan_Seq
    AS INT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    NO CYCLE;

CREATE SEQUENCE dbo.DH_Seq
    AS INT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    NO CYCLE;

CREATE SEQUENCE dbo.DC_Seq
    AS INT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    NO CYCLE;

CREATE SEQUENCE dbo.MA_Seq
    AS INT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    NO CYCLE;

CREATE SEQUENCE dbo.NV_Seq
    AS INT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    NO CYCLE;


-- Bảng Ban
CREATE TABLE [dbo].[Ban](
	[idBA] [INT] IDENTITY(1,1) PRIMARY KEY, -- New primary key
	[MaBan] [varchar](6) NOT NULL UNIQUE, -- Original primary key now unique
	[ViTri] VARCHAR(50) NOT NULL,
	[SoGhe] [int] DEFAULT 0,
	[GhiChu] [nvarchar](200) NULL
);
GO
ALTER TABLE [dbo].[Ban] ADD CONSTRAINT DF_Ban_MaBan
    DEFAULT ('BA' + FORMAT(NEXT VALUE FOR dbo.Ban_Seq, '0000')) FOR [MaBan];
GO


-- Bảng DungCu
CREATE TABLE [dbo].[DungCu](
	[idDC] [INT] IDENTITY(1,1) PRIMARY KEY, -- New primary key
	[MaDungCu] [varchar](6) NOT NULL UNIQUE, -- Original primary key now unique
	[TenDungCu] [nvarchar](50) NULL,
	[Loai] [nvarchar](50) NULL,
	[SoLuong] [int] NULL,
	[TinhTrang] [nvarchar](50) NULL,
	[NgayThongKe] [DATETIME] NOT NULL DEFAULT CURRENT_TIMESTAMP
);
GO
ALTER TABLE [dbo].[DungCu] ADD CONSTRAINT DF_DungCu_MaDungCu
    DEFAULT ('DC' + FORMAT(NEXT VALUE FOR dbo.DC_Seq, '0000')) FOR [MaDungCu];
GO


-- Bảng NhanVien
CREATE TABLE [dbo].[NhanVien](
	[idNV] [INT] IDENTITY(1,1) PRIMARY KEY, -- New primary key
	[MaNV] [varchar](6) NOT NULL UNIQUE, -- Original primary key now unique
	[TenNV] [nvarchar](50) NOT NULL,
	[NgaySinh] [DATETIME] NULL,
	[SDT] [varchar](15) NOT NULL,
	[Email] [varchar](255) NULL,
	[ChucVu] [nvarchar](50) NOT NULL,
	[Luong] [money] NULL
);
GO
ALTER TABLE [dbo].[NhanVien] ADD CONSTRAINT DF_NhanVien_MaNV
    DEFAULT ('NV' + FORMAT(NEXT VALUE FOR dbo.NV_Seq, '0000')) FOR [MaNV];
GO


-- Bảng MonAn
CREATE TABLE [dbo].[MonAn](
        [id] [INT] IDENTITY(1,1) PRIMARY KEY, -- Already existing primary key
	[MaMon] [varchar](6) NOT NULL UNIQUE, -- Already existing unique constraint
	[TenMon] [nvarchar](100) NOT NULL,
	[HinhAnh] VARBINARY(MAX) NULL,
	[Gia] [money] NOT NULL,
	[DonVi] NVARCHAR(10) NOT NULL,
	[Nhom] NVARCHAR(25) NOT NULL,
	[MonAnKem] INT NULL,
	[TrangThai] [bit] NOT NULL DEFAULT 1 -- DEFAULT 1 (sẵn sàng)
);
GO
ALTER TABLE [dbo].[MonAn] ADD CONSTRAINT DF_MonAn_MaMon
    DEFAULT ('MA' + FORMAT(NEXT VALUE FOR dbo.MA_Seq, '0000')) FOR [MaMon];
GO
ALTER TABLE [dbo].[MonAn] WITH CHECK ADD CONSTRAINT [FK_MonAn_MonAn] FOREIGN KEY([MonAnKem])
REFERENCES [dbo].[MonAn] ([id]);
GO
ALTER TABLE [dbo].[MonAn] CHECK CONSTRAINT [FK_MonAn_MonAn];
GO


-- Bảng DatBan
CREATE TABLE [dbo].[DatBan](
	[idDB] [INT] IDENTITY(1,1) PRIMARY KEY, -- New primary key
	[MaDatBan] [varchar](6) NOT NULL UNIQUE, -- Original primary key now unique
	[MaBan] [varchar](6) NOT NULL, -- This will now reference Ban.MaBan (unique) or Ban.idBA (PK)
	[TenKhach] [nvarchar](100) NULL,
	[SoKhach] [int] NOT NULL,
	[SDTKhach] [nvarchar](20) NOT NULL,
	[Email] [varchar](50) NULL,
	[ThoiGianDat] [DATETIME] NOT NULL DEFAULT CURRENT_TIMESTAMP,
	[TrangThai] [nvarchar](50) NULL,
	[GhiChu] [nvarchar](200) NULL
);
GO
ALTER TABLE [dbo].[DatBan] ADD CONSTRAINT DF_DatBan_MaDatBan
    DEFAULT ('DB' + FORMAT(NEXT VALUE FOR dbo.DBan_Seq, '0000')) FOR [MaDatBan];
GO
-- Removed old FK and added new one referencing idBA
ALTER TABLE [dbo].[DatBan] WITH CHECK ADD CONSTRAINT [FK_DatBan_Ban] FOREIGN KEY([MaBan])
REFERENCES [dbo].[Ban] ([MaBan]); -- Keep referencing MaBan as it's unique
GO
ALTER TABLE [dbo].[DatBan] CHECK CONSTRAINT [FK_DatBan_Ban];
GO

-- Bảng TaiKhoan
CREATE TABLE [dbo].[TaiKhoan](
	[idTK] [INT] IDENTITY(1,1) PRIMARY KEY, -- New primary key
	[MaNV] [varchar](6) NOT NULL, -- This will now reference NhanVien.MaNV (unique)
	[TenTK] [varchar](50) NOT NULL,
    [MatKhau] [varchar](50) NOT NULL,
    CONSTRAINT [UQ_TaiKhoan_MaNV_TenTK] UNIQUE CLUSTERED ([MaNV] ASC, [TenTK] ASC) -- Original composite PK now unique
);
GO
-- Removed old FK and added new one referencing MaNV
ALTER TABLE [dbo].[TaiKhoan] WITH CHECK ADD CONSTRAINT [FK_TaiKhoan_NhanVien] FOREIGN KEY([MaNV])
REFERENCES [dbo].[NhanVien] ([MaNV]); -- Keep referencing MaNV as it's unique
GO
ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [FK_TaiKhoan_NhanVien];
GO


-- Bảng DonHang
CREATE TABLE [dbo].[DonHang](
	[idDH] [INT] IDENTITY(1,1) PRIMARY KEY,
	[MaDonHang] [varchar](6) NOT NULL UNIQUE,
	[MaDatBan] [varchar](6) NOT NULL,
	[MaNV] [varchar](6) NOT NULL,
	[ThoiGianTao] [DATETIME] NULL DEFAULT  CURRENT_TIMESTAMP,
	[ThoiGianThanhToan] [DATETIME] NULL
);
GO
ALTER TABLE [dbo].[DonHang] ADD CONSTRAINT DF_DonHang_MaDonHang
    DEFAULT ('DH' + FORMAT(NEXT VALUE FOR dbo.DH_Seq, '0000')) FOR [MaDonHang];
GO
-- Removed old FKs and added new ones referencing MaDatBan and MaNV
ALTER TABLE [dbo].[DonHang] WITH CHECK ADD CONSTRAINT [FK_DonHang_DatBan] FOREIGN KEY([MaDatBan])
REFERENCES [dbo].[DatBan] ([MaDatBan]); -- Keep referencing MaDatBan as it's unique
GO
ALTER TABLE [dbo].[DonHang] CHECK CONSTRAINT [FK_DonHang_DatBan];
GO
ALTER TABLE [dbo].[DonHang] WITH CHECK ADD CONSTRAINT [FK_DonHang_NhanVien] FOREIGN KEY([MaNV])
REFERENCES [dbo].[NhanVien] ([MaNV]); -- Keep referencing MaNV as it's unique
GO
ALTER TABLE [dbo].[DonHang] CHECK CONSTRAINT [FK_DonHang_NhanVien];
GO


-- Bảng ChiTietDonHang
CREATE TABLE [dbo].[ChiTietDonHang](
	[idCTDH] [INT] IDENTITY(1,1) PRIMARY KEY, -- New primary key
	[MaDonHang] [varchar](6) NOT NULL, -- This will now reference DonHang.MaDonHang (unique)
	[MaMon] [varchar](6) NOT NULL, -- This will now reference MonAn.MaMon (unique)
	[SoLuong] [int] NULL,
    CONSTRAINT [UQ_ChiTietDonHang_MaDonHang_MaMon] UNIQUE CLUSTERED ([MaDonHang] ASC, [MaMon] ASC) -- Original composite PK now unique
);
GO
-- Removed old FKs and added new ones referencing MaDonHang and MaMon
ALTER TABLE [dbo].[ChiTietDonHang] WITH CHECK ADD CONSTRAINT [FK_ChiTietDonHang_DonHang] FOREIGN KEY([MaDonHang])
REFERENCES [dbo].[DonHang] ([MaDonHang]); -- Keep referencing MaDonHang as it's unique
GO
ALTER TABLE [dbo].[ChiTietDonHang] CHECK CONSTRAINT [FK_ChiTietDonHang_DonHang];
GO
ALTER TABLE [dbo].[ChiTietDonHang] WITH CHECK ADD CONSTRAINT [FK_ChiTietDonHang_MonAn] FOREIGN KEY([MaMon])
REFERENCES [dbo].[MonAn] ([MaMon]); -- Keep referencing MaMon as it's unique
GO
ALTER TABLE [dbo].[ChiTietDonHang] CHECK CONSTRAINT [FK_ChiTietDonHang_MonAn];
GO
