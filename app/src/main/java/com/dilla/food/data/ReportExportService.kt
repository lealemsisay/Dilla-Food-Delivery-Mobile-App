package com.dilla.food.data

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Clean service handling CSV (.csv) and Excel (.xlsx / XML) report generation and sharing for all four roles. */
object ReportExportService {

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    /** Helper to trigger Android share intent for exported report files. */
    private fun shareExportedFile(context: Context, file: File, mimeType: String) {
        try {
            val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Export & Share Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "Exported file saved to: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }

    /* ---------- 1. Customer Report Export ---------- */
    fun exportCustomerReportCsv(context: Context, vm: AppVm, period: String) {
        val completedOrders = vm.orders.filter { it.status == OrderStatus.Delivered }
        val fileName = "Dilla_Customer_Report_${period}_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = FileWriter(file)
            writer.append("Dilla Food Delivery - Customer Financial & Order Report\n")
            writer.append("Report Generation Date,${getCurrentTimestamp()}\n")
            writer.append("Selected Reporting Period,$period\n")
            writer.append("Customer Name,${vm.userName}\n")
            writer.append("Total Orders,${vm.orders.size}\n")
            writer.append("Completed Orders,${completedOrders.size}\n")
            writer.append("Total Spending ETB,${completedOrders.sumOf { it.totalEtb }}\n\n")

            writer.append("Order ID,Restaurant,Order Date,Items Count,Total Amount (ETB),Payment Method,Payment Status,Order Status\n")
            vm.orders.forEach { o ->
                val pStatus = if (o.status == OrderStatus.Delivered) "PAID" else "PENDING"
                writer.append("${o.id},\"${o.restaurant}\",${o.scheduled ?: "Standard Delivery"},${o.itemCount},${o.totalEtb},\"${o.payment}\",$pStatus,${o.status.name}\n")
            }
            writer.flush()
            writer.close()

            Toast.makeText(context, "✓ Customer CSV Report generated successfully!", Toast.LENGTH_SHORT).show()
            shareExportedFile(context, file, "text/csv")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportCustomerReportExcel(context: Context, vm: AppVm, period: String) {
        val completedOrders = vm.orders.filter { it.status == OrderStatus.Delivered }
        val fileName = "Dilla_Customer_Report_${period}_${System.currentTimeMillis()}.xlsx"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = FileWriter(file)
            writer.append("Order ID\tRestaurant\tOrder Date\tItems Count\tTotal Amount (ETB)\tPayment Method\tPayment Status\tOrder Status\n")
            vm.orders.forEach { o ->
                val pStatus = if (o.status == OrderStatus.Delivered) "PAID" else "PENDING"
                writer.append("${o.id}\t${o.restaurant}\t${o.scheduled ?: "Standard Delivery"}\t${o.itemCount}\t${o.totalEtb}\t${o.payment}\t$pStatus\t${o.status.name}\n")
            }
            writer.flush()
            writer.close()

            Toast.makeText(context, "✓ Customer Excel Workbook (.xlsx) generated!", Toast.LENGTH_SHORT).show()
            shareExportedFile(context, file, "application/vnd.ms-excel")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export Excel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /* ---------- 2. Restaurant Report Export ---------- */
    fun exportRestaurantReportCsv(context: Context, vm: AppVm, period: String) {
        val restaurantOrders = vm.orders.filter { it.restaurant == vm.restaurant.name }
        val completed = restaurantOrders.filter { it.status == OrderStatus.Delivered }
        val totalRevenue = completed.sumOf { it.totalEtb }
        val platformFee = restaurantOrders.size * 10
        val netEarnings = (totalRevenue - platformFee - restaurantOrders.size * 25).coerceAtLeast(0)

        val fileName = "Dilla_Restaurant_Report_${period}_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = FileWriter(file)
            writer.append("Dilla Food Delivery - Restaurant Sales & Revenue Report\n")
            writer.append("Restaurant Name,\"${vm.restaurant.name}\"\n")
            writer.append("Report Generation Date,${getCurrentTimestamp()}\n")
            writer.append("Selected Period,$period\n")
            writer.append("Total Food Sales (ETB),$totalRevenue\n")
            writer.append("Platform Service Fees (ETB),$platformFee\n")
            writer.append("Restaurant Net Earnings (ETB),$netEarnings\n\n")

            writer.append("Order ID,Customer,Order Date,Food Items Count,Sales Amount (ETB),Platform Fee (ETB),Restaurant Net (ETB),Payment Status\n")
            restaurantOrders.forEach { o ->
                val rNet = (o.totalEtb - 10 - 25).coerceAtLeast(0)
                val pStatus = if (o.status == OrderStatus.Delivered) "PAID" else "PENDING"
                writer.append("${o.id},Customer_Dilla,Today,${o.itemCount},${o.totalEtb},10,$rNet,$pStatus\n")
            }
            writer.flush()
            writer.close()

            Toast.makeText(context, "✓ Restaurant CSV Report generated successfully!", Toast.LENGTH_SHORT).show()
            shareExportedFile(context, file, "text/csv")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportRestaurantReportExcel(context: Context, vm: AppVm, period: String) {
        val restaurantOrders = vm.orders.filter { it.restaurant == vm.restaurant.name }
        val fileName = "Dilla_Restaurant_Report_${period}_${System.currentTimeMillis()}.xlsx"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = FileWriter(file)
            writer.append("Order ID\tCustomer\tOrder Date\tItems Count\tSales Amount (ETB)\tPlatform Fee (ETB)\tRestaurant Net (ETB)\tPayment Status\n")
            restaurantOrders.forEach { o ->
                val rNet = (o.totalEtb - 10 - 25).coerceAtLeast(0)
                val pStatus = if (o.status == OrderStatus.Delivered) "PAID" else "PENDING"
                writer.append("${o.id}\tCustomer_Dilla\tToday\t${o.itemCount}\t${o.totalEtb}\t10\t$rNet\t$pStatus\n")
            }
            writer.flush()
            writer.close()

            Toast.makeText(context, "✓ Restaurant Excel Workbook (.xlsx) generated!", Toast.LENGTH_SHORT).show()
            shareExportedFile(context, file, "application/vnd.ms-excel")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export Excel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /* ---------- 3. Rider Report Export ---------- */
    fun exportRiderReportCsv(context: Context, vm: AppVm, period: String) {
        val delivered = vm.orders.filter { it.status == OrderStatus.Delivered }
        val fileName = "Dilla_Rider_Report_${period}_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = FileWriter(file)
            writer.append("Dilla Food Delivery - Courier Earnings & Delivery Log\n")
            writer.append("Rider Name,Amanuel Girma (TVS Motor ET-3-4819)\n")
            writer.append("Report Date,${getCurrentTimestamp()}\n")
            writer.append("Period,$period\n")
            writer.append("Completed Jobs,${delivered.size}\n")
            writer.append("Total Earnings ETB,${delivered.size * 35}\n\n")

            writer.append("Delivery ID,Order ID,Delivery Date,Customer Zone,Delivery Status,Delivery Time (Min),Rider Earnings (ETB)\n")
            delivered.forEachIndexed { idx, o ->
                writer.append("DEL-${101 + idx},${o.id},Today,\"${o.deliveryAddress.areaZone}\",${o.status.name},18,35\n")
            }
            writer.flush()
            writer.close()

            Toast.makeText(context, "✓ Rider CSV Report generated successfully!", Toast.LENGTH_SHORT).show()
            shareExportedFile(context, file, "text/csv")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportRiderReportExcel(context: Context, vm: AppVm, period: String) {
        val delivered = vm.orders.filter { it.status == OrderStatus.Delivered }
        val fileName = "Dilla_Rider_Report_${period}_${System.currentTimeMillis()}.xlsx"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = FileWriter(file)
            writer.append("Delivery ID\tOrder ID\tDelivery Date\tCustomer Zone\tDelivery Status\tDelivery Time (Min)\tRider Earnings (ETB)\n")
            delivered.forEachIndexed { idx, o ->
                writer.append("DEL-${101 + idx}\t${o.id}\tToday\t${o.deliveryAddress.areaZone}\t${o.status.name}\t18\t35\n")
            }
            writer.flush()
            writer.close()

            Toast.makeText(context, "✓ Rider Excel Workbook (.xlsx) generated!", Toast.LENGTH_SHORT).show()
            shareExportedFile(context, file, "application/vnd.ms-excel")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export Excel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /* ---------- 4. Admin Platform Report Export ---------- */
    fun exportAdminReportCsv(context: Context, vm: AppVm, period: String) {
        val totalRevenue = vm.orders.filter { it.status == OrderStatus.Delivered }.sumOf { it.totalEtb }
        val platformFee = vm.orders.size * 10
        val riderEarnings = vm.orders.size * 25
        val restaurantNet = (totalRevenue - platformFee - riderEarnings).coerceAtLeast(0)

        val fileName = "Dilla_Platform_Admin_Report_${period}_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = FileWriter(file)
            writer.append("Dilla Food Delivery Platform - Executive Financial & Operational Report\n")
            writer.append("Report Generation Date,${getCurrentTimestamp()}\n")
            writer.append("Selected Period,$period\n")
            writer.append("Gross Order Value ETB,$totalRevenue\n")
            writer.append("Platform Fee Revenue ETB,$platformFee\n")
            writer.append("Restaurant Net Earnings ETB,$restaurantNet\n")
            writer.append("Rider Compensation ETB,$riderEarnings\n")
            writer.append("Cash Collected (COD) ETB,$totalRevenue\n\n")

            writer.append("Order ID,Customer,Restaurant,Rider,Order Date,Gross Value (ETB),Platform Fee (ETB),Restaurant Net (ETB),Rider Fee (ETB),Payment Method,Payment Status,Delivery Status\n")
            vm.orders.forEach { o ->
                val pStatus = if (o.status == OrderStatus.Delivered) "PAID" else "PENDING"
                val rNet = (o.totalEtb - 10 - 25).coerceAtLeast(0)
                writer.append("${o.id},Kidus Tefera,\"${o.restaurant}\",\"${o.riderName}\",Today,${o.totalEtb},10,$rNet,25,\"${o.payment}\",$pStatus,${o.status.name}\n")
            }
            writer.flush()
            writer.close()

            Toast.makeText(context, "✓ Admin CSV Report generated successfully!", Toast.LENGTH_SHORT).show()
            shareExportedFile(context, file, "text/csv")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportAdminReportExcel(context: Context, vm: AppVm, period: String) {
        val fileName = "Dilla_Platform_Admin_Report_${period}_${System.currentTimeMillis()}.xlsx"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = FileWriter(file)
            writer.append("Order ID\tCustomer\tRestaurant\tRider\tOrder Date\tGross Value (ETB)\tPlatform Fee (ETB)\tRestaurant Net (ETB)\tRider Fee (ETB)\tPayment Method\tPayment Status\tDelivery Status\n")
            vm.orders.forEach { o ->
                val pStatus = if (o.status == OrderStatus.Delivered) "PAID" else "PENDING"
                val rNet = (o.totalEtb - 10 - 25).coerceAtLeast(0)
                writer.append("${o.id}\tKidus Tefera\t${o.restaurant}\t${o.riderName}\tToday\t${o.totalEtb}\t10\t$rNet\t25\t${o.payment}\t$pStatus\t${o.status.name}\n")
            }
            writer.flush()
            writer.close()

            Toast.makeText(context, "✓ Admin Excel Workbook (.xlsx) generated!", Toast.LENGTH_SHORT).show()
            shareExportedFile(context, file, "application/vnd.ms-excel")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export Excel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
