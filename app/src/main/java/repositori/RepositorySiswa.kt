package repositori

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import modeldata.Siswa


interface RepositorySiswa {
    suspend fun getDataSiswa(): List<Siswa>
    suspend fun postDataSiswa(siswa: Siswa)
    suspend fun getSiswaById(id: Long): Siswa
    suspend fun updateSiswa(siswa: Siswa)
    suspend fun deleteSiswa(siswa: Siswa)
}

class FirebaseRepositorySiswa : RepositorySiswa {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("siswa")

    override suspend fun getDataSiswa(): List<Siswa> {
        return try {
            collection.get().await().documents.map { doc ->
                Siswa(
                    id = doc.getLong("id")?.toLong() ?: 0L,
                    nama = doc.getString("nama") ?: "",
                    alamat = doc.getString("alamat") ?: "",
                    telpon = doc.getString("telpon") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun postDataSiswa(siswa: Siswa) {
        val docRef = collection.document(siswa.id.toString())
        val data = hashMapOf(
            "id" to siswa.id,
            "nama" to siswa.nama,
            "alamat" to siswa.alamat,
            "telpon" to siswa.telpon
        )
        docRef.set(data).await()
    }

    override suspend fun getSiswaById(id: Long): Siswa {
        return try {
            val documentSnapshot = collection.document(id.toString()).get().await()
            Siswa(
                id = documentSnapshot.getLong("id")?.toLong() ?: 0L,
                nama = documentSnapshot.getString("nama") ?: "",
                alamat = documentSnapshot.getString("alamat") ?: "",
                telpon = documentSnapshot.getString("telpon") ?: ""
            )
        } catch (e: Exception) {
            Siswa()
        }
    }

    override suspend fun updateSiswa(siswa: Siswa) {
        postDataSiswa(siswa)
    }

    override suspend fun deleteSiswa(siswa: Siswa) {
        try {
            collection.document(siswa.id.toString()).delete().await()
        } catch (e: Exception) {
            throw Exception("Gagal menghapus data siswa: ${e.message}")
        }
    }
}


