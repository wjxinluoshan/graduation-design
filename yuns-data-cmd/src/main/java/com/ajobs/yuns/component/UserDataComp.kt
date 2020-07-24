package com.ajobs.yuns.component

import com.ajobs.yuns.mapperImp.ArticleCommentMapperImp
import com.ajobs.yuns.mapperImp.ArticleMapperImp
import com.ajobs.yuns.mapperImp.ResourceNameMapperImp
import com.ajobs.yuns.mapperImp.UserMapperImp
import com.ajobs.yuns.mapperImp.hall.HallArticleMapperImp
import com.ajobs.yuns.mapperImp.hall.HallResMapperImp
import com.ajobs.yuns.tool.FileHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.ArrayList


/**
 * 数据资源的文件的操作（add and delete）
 */
@Component
class UserDataComp {


    @Autowired
    private lateinit var resourceNameMapperImp: ResourceNameMapperImp

    @Autowired
    private lateinit var articleMapperImp: ArticleMapperImp

    @Autowired
    private lateinit var articleCommentMapperImp: ArticleCommentMapperImp

    @Autowired
    @Qualifier("dbTransactionManager")
    private lateinit var dbTransactionManager: DataSourceTransactionManager
    @Autowired
    @Qualifier("dbTransactionManagerArt")
    private lateinit var dbTransactionManagerArt: DataSourceTransactionManager

    @Autowired
    private lateinit var hallResMapperImp: HallResMapperImp

    @Autowired
    private lateinit var hallArticleMapperImp: HallArticleMapperImp

    @Autowired
    private lateinit var userMapperImp: UserMapperImp

    @Autowired
    private lateinit var npl: Npl

    @Autowired
    private lateinit var executor: ExecutorService

    private val pic_name = "pic_name"
    private val doc_name = "doc_name"
    private val resource_name = "resource_name"

    /**
     * 创建user fileType文件夹
     */
    private fun createUserDir(userRootFile: File, id: String, fileType: String): File {
        var file: File?
        userRootFile.let {
            File(it.path, id).let {
                file = File(it.path, fileType).apply {
                    if (!this.exists()) {
                        mkdirs()
                    }
                }
            }
        }
        return file!!
    }


    /**
     * 生成文件：  /static/user/(userId)/(fileType)
     */
    fun uploadFiles(files: Array<MultipartFile>,
                    id: String, fileType: String) {
        var userRootFile = File(FileHelper.getClasspath(), FileHelper.getUserPath())
        //创建当前user的id/file文件夹
        var file: File? = createUserDir(userRootFile, id, fileType)
        /**
         * npl test
         */
        var dates = CopyOnWriteArrayList<String>()
        var tags = CopyOnWriteArrayList<String>()
        var futures = ArrayList<Future<Int>>(files.size)

        var storage = AtomicLong(userMapperImp.userInfo(id.toInt()).storage.toLong())

        files.let {
            //遍历上传的文件
            it.forEach {
                var mutilpartFile = it
                var fileName = (System.currentTimeMillis() + Random().nextInt(Int.MAX_VALUE)).toString() + "_" + System.currentTimeMillis() + "." + mutilpartFile.originalFilename.split(".").run {
                    this[this.size - 1]
                }
                var newFile = File(file!!.path, fileName).apply {
                    if (!exists())
                        createNewFile()
                }
                futures.add(executor.submit({
                    FileOutputStream(newFile).use {
                        when (fileType) {
                            "doc" -> {
                                val transactionTemplate = TransactionTemplate(dbTransactionManager)
                                transactionTemplate.execute { transactionStatus ->
                                    try {
                                        /**
                                         * npl test
                                         */
                                        resourceNameMapperImp.insert(id.toInt(), doc_name, "/yuns/user/${id}/doc/${fileName}?name=${mutilpartFile.originalFilename}")
                                        it.write(mutilpartFile.bytes)
                                        dates.add(mutilpartFile.originalFilename)
                                        tags.add("/yuns/user/${id}/doc/${fileName}?name=${mutilpartFile.originalFilename}")
                                        storage.addAndGet(mutilpartFile.size / 1024)
                                    } catch (e: java.lang.Exception) {
                                        transactionStatus.setRollbackOnly()
                                        throw e
                                    }
                                }
                            }
                            "ores" -> {
                                val transactionTemplate = TransactionTemplate(dbTransactionManager)
                                transactionTemplate.execute { transactionStatus ->
                                    try {
                                        /**
                                         * npl test
                                         */
                                        resourceNameMapperImp.insert(id.toInt(), resource_name, "/yuns/user/${id}/ores/${fileName}?name=${mutilpartFile.originalFilename}")
                                        it.write(mutilpartFile.bytes)
//                                        synchronized(filesName) {
//                                            storage += (mutilpartFile.size / 1024)
//                                            userMapperImp.updateUser(mutableListOf(storage), mutableListOf("storage")
//                                                    , mutableListOf("id"), mutableListOf(id.toInt()))
//                                        }
                                        dates.add(mutilpartFile.originalFilename)
                                        tags.add("/yuns/user/${id}/ores/${fileName}?name=${mutilpartFile.originalFilename}")
                                        storage.addAndGet(mutilpartFile.size / 1024)
                                    } catch (e: java.lang.Exception) {
                                        transactionStatus.setRollbackOnly()
                                        throw e
                                    }
                                }
                            }
                            "pic" -> {
                                val transactionTemplate = TransactionTemplate(dbTransactionManager)
                                transactionTemplate.execute { transactionStatus ->
                                    try {
                                        /**
                                         * npl test
                                         */
                                        it.write(mutilpartFile.bytes)
                                        resourceNameMapperImp.insert(id.toInt(), pic_name, "/yuns/user/${id}/pic/${fileName}?name=${mutilpartFile.originalFilename}")
                                        dates.add(mutilpartFile.originalFilename)
                                        tags.add("/yuns/user/${id}/pic/${fileName}?name=${mutilpartFile.originalFilename}")
                                        storage.addAndGet(mutilpartFile.size / 1024)
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                        newFile.delete()
                                        transactionStatus.setRollbackOnly()
                                        throw e
                                    }
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }, 1))
            }
        }

        /**
         *
         */
        for (future in futures) {
            try {
                future.get()
            } catch (e: Exception) {
            }

        }
        executor.execute {
            userMapperImp.updateUser(mutableListOf(storage.get()), mutableListOf("storage")
                    , mutableListOf("id"), mutableListOf(id.toInt()))
        }
        /**
         * npl test
         */
        when (fileType) {
            "doc" -> {
                npl.createUserDocument(dates, tags, id, npl.DOC)
            }
            "ores" -> {
                npl.createUserDocument(dates, tags, id, npl.RES)
            }
            "pic" -> {
                npl.createUserDocument(dates, tags, id, npl.PICTURE)
            }
            else -> {
            }
        }
    }


    /**
     * 删除文件:异步
     */
    fun delFiles(filesName: Array<String>, id: String, fileType: String) {
        var dataFile = File("${FileHelper.getClasspath()}/${FileHelper.getUserPath()}", "${id}/${fileType}")

        var tags = CopyOnWriteArrayList<String>()

        var futures = ArrayList<Future<Int>>(filesName.size)
        var storage = userMapperImp.userInfo(id.toInt()).storage
        var fileSizes = CopyOnWriteArrayList<Long>()
        filesName.forEach {
            futures.add(executor.submit({
                var delFile: File?
                delFile = if (fileType == "article") {
                    var arr = it.split("/")
                    File(dataFile.path, arr[arr.size - 1])
                } else
                    File(dataFile.path, it.run {
                        var arr = it.split("?name=")[0].split("/")
                        arr[arr.size - 1]
                    })
                when (fileType) {
                    "doc" -> {
                        val transactionTemplate = TransactionTemplate(dbTransactionManager)
                        transactionTemplate.execute { transactionStatus ->
                            try {
                                resourceNameMapperImp.delResourceName(id.toInt(), doc_name, it)
                                hallResMapperImp.delSingleDoc(it + "%", id)
                                fileSizes.add(delFile.length() / 1024)
                                delFile.delete()
                                tags.add(it)
                            } catch (e: java.lang.Exception) {
                                transactionStatus.setRollbackOnly()
                                throw e
                            }
                        }

                    }
                    "ores" -> {
                        val transactionTemplate = TransactionTemplate(dbTransactionManager)
                        transactionTemplate.execute { transactionStatus ->
                            try {
                                resourceNameMapperImp.delResourceName(id.toInt(), resource_name, it)
                                hallResMapperImp.delSingleOres(it + "%", id)
                                fileSizes.add(delFile.length() / 1024)
                                delFile.delete()
                                tags.add(it)
                            } catch (e: java.lang.Exception) {
                                transactionStatus.setRollbackOnly()
                                throw e
                            }
                        }
                    }
                    "pic" -> {
                        val transactionTemplate = TransactionTemplate(dbTransactionManager)
                        transactionTemplate.execute { transactionStatus ->
                            try {
                                resourceNameMapperImp.delResourceName(id.toInt(), pic_name, it)
                                hallResMapperImp.delSinglePic(it + "%", id)
                                fileSizes.add(delFile.length() / 1024)
                                delFile.delete()
                                tags.add(it)
                            } catch (e: java.lang.Exception) {
                                transactionStatus.setRollbackOnly()
                                throw e
                            }
                        }
                    }
                    "article" -> {
                        val transactionTemplate = TransactionTemplate(dbTransactionManager)
                        transactionTemplate.execute<Any?> { txStatus: TransactionStatus ->
                            try {
                                articleMapperImp.delArticleLink(id.toInt(), it)
                                var articleLinkName = it.run {
                                    var arr = this.split("/")
                                    arr[arr.size - 1].replace(".html", "")
                                }
                                var transactionTemplateArt = TransactionTemplate(dbTransactionManagerArt)
                                if (!transactionTemplateArt.execute { transactionStatus ->
                                            try {
                                                if (articleCommentMapperImp.checkTableWhetherExisted("_${id}_${articleLinkName}") != null) {
                                                    articleCommentMapperImp.delArticleCommentTable(id.toInt(), articleLinkName)
                                                }
                                                if (hallArticleMapperImp.checkTableWhetherExisted() != null)
                                                    //这里包含了删除大厅文章的lucene index
                                                    hallArticleMapperImp.delSingleArt("${articleLinkName}.html", id)
                                                true
                                            } catch (e: java.lang.Exception) {
                                                transactionStatus.setRollbackOnly()
                                                false
                                            }
                                        }!!) {
                                } else {
                                    fileSizes.add(delFile.length() / 1024)
                                    delFile.delete()
                                    tags.add("/yuns/user/${id}/article/${articleLinkName}.html")
                                }
                            } catch (e: Exception) {
                                txStatus.setRollbackOnly()
                                throw e
                            }
                        }
//                    articleMapperImp.delArticleLink(id.toInt(), it)
//                    var articleLinkName = it.run {
//                        var arr = this.split("/")
//                        arr[arr.size - 1].replace(".html", "")
//                    }
//                    if (articleCommentMapperImp.checkTableWhetherExisted("_${id}_${articleLinkName}") != null)
//                        articleCommentMapperImp.delArticleCommentTable(id.toInt(), articleLinkName)
                    }
                }
            }, 1))

        }

        for (future in futures) {
            try {
                future.get()
            } catch (e: java.lang.Exception) {
            }
        }
        executor.execute {
            userMapperImp.updateUser(mutableListOf(storage - fileSizes.sum()), mutableListOf("storage")
                    , mutableListOf("id"), mutableListOf(id.toInt()))
        }
        //   futures.clear()
        /**
         * npl test
         */
        when (fileType) {
            "doc" -> {
                npl.deleteOrUpdateUserDocument(tags, id, npl.DOC, false)
            }
            "ores" -> {
                npl.deleteOrUpdateUserDocument(tags, id, npl.RES, false)
            }
            "pic" -> {
                npl.deleteOrUpdateUserDocument(tags, id, npl.PICTURE, false)
            }
            "article" -> {
//                futures.add(executor.submit({
//                    try {
                npl.deleteOrUpdateUserDocument(tags, id, npl.ARTICLE, false)
//                    } catch (e: Exception) {
//                    }
//                }, 1))
//                futures.add(executor.submit({
//                    npl.deleteOrUpdateHallDocument(tags, npl.ARTICLE, false)
//                }, 1))
//                for (future in futures) {
//                    future.get()
//                }
            }
        }

    }

}