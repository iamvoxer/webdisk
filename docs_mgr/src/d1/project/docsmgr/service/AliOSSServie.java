package d1.project.docsmgr.service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import d1.project.docsmgr.model.AliOSSAuth;
import d1.project.docsmgr.model.AliOSSFile;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AliOSSServie {
    private static AliOSSServie instance;

    private AliOSSServie() {

    }

    public static AliOSSServie getInstance() {
        if (instance == null)
            instance = new AliOSSServie();
        return instance;
    }

    public void delFile(AliOSSAuth auth, String path) {
        OSSClient ossClient = new OSSClient(auth.getEndPoint(), auth.getAccessKeyId(), auth.getAccessKeySerect());
        ossClient.deleteObject(auth.getBucket(), path);
        ossClient.shutdown();
    }

    public void downloadFileWithProgress(AliOSSAuth auth, String key, String fileName, ProgressBar bar) {
        OSSClient ossClient = new OSSClient(auth.getEndPoint(), auth.getAccessKeyId(), auth.getAccessKeySerect());
        // 带进度条的下载。
        ossClient.getObject(new GetObjectRequest(auth.getBucket(), key).
                        <GetObjectRequest>withProgressListener(new GetObjectProgressListener(bar)),
                new File(fileName));
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    public void uploadFileWithPart(AliOSSAuth auth, String fileName, String key, ProgressBar bar, boolean hasUpdateRole) throws Exception {
        OSSClient ossClient = new OSSClient(auth.getEndPoint(), auth.getAccessKeyId(), auth.getAccessKeySerect());
        if (!hasUpdateRole) {
            boolean found = ossClient.doesObjectExist(auth.getBucket(), key);
            if (found) {
                ossClient.shutdown();
                throw new Exception("没有更新权限导致更新已存在的文件失败：" + key);
            }
        }
        InputStream inputStream = new FileInputStream(fileName);
        /* 步骤1：初始化一个分片上传事件。
         */
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(auth.getBucket(), key);
        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个ID来发起相关的操作，如取消分片上传、查询分片上传等。
        String uploadId = result.getUploadId();

        /* 步骤2：上传分片。
         */
        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partETags = new ArrayList<PartETag>();
        // 计算文件有多少个分片。
        final long partSize = 1 * 1024 * 1024L;   // 1MB
        final File sampleFile = new File(fileName);
        long fileLength = sampleFile.length();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }
        // 遍历分片上传。
        for (int i = 0; i < partCount; i++) {
            final int index = i;
            final int part = partCount;
            if (bar != null) {
                Platform.runLater(() -> {
                    bar.setProgress((double) index / (double) part);
                });
            }
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
            InputStream instream = new FileInputStream(sampleFile);
            // 跳过已经上传的分片。
            instream.skip(startPos);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(auth.getBucket());
            uploadPartRequest.setKey(key);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(instream);
            // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100KB。
            uploadPartRequest.setPartSize(curPartSize);
            // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
            uploadPartRequest.setPartNumber(i + 1);
            // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
            // 每次上传分片之后，OSS的返回结果会包含一个PartETag。PartETag将被保存到partETags中。
            partETags.add(uploadPartResult.getPartETag());
        }

        /* 步骤3：完成分片上传。
         */
        // 排序。partETags必须按分片号升序排列。
        Collections.sort(partETags, new Comparator<PartETag>() {
            public int compare(PartETag p1, PartETag p2) {
                return p1.getPartNumber() - p2.getPartNumber();
            }
        });
        // 在执行该操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(auth.getBucket(), key, uploadId, partETags);
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        ossClient.shutdown();
    }

    public void newFolder(AliOSSAuth auth, String root, String folderName) {
        OSSClient ossClient = new OSSClient(auth.getEndPoint(), auth.getAccessKeyId(), auth.getAccessKeySerect());
        String key = root + folderName + "/";
        ossClient.putObject(auth.getBucket(), key, new ByteArrayInputStream(new byte[]{}));
        ossClient.shutdown();
    }

    public void uploadFolder(AliOSSAuth auth, String key) {
        OSSClient ossClient = new OSSClient(auth.getEndPoint(), auth.getAccessKeyId(), auth.getAccessKeySerect());
        ossClient.putObject(auth.getBucket(), key, new ByteArrayInputStream(new byte[]{}));
        ossClient.shutdown();
    }

    //递归列出所有目录下的文件名
    public List<String> listAllFilesStringInFolder(AliOSSAuth auth, String perfix) {
        List<String> files = new ArrayList<>();
        OSSClient ossClient = new OSSClient(auth.getEndPoint(), auth.getAccessKeyId(), auth.getAccessKeySerect());
        getFiles(auth, perfix, files, ossClient);
        ossClient.shutdown();
        return files;
    }


    private void getFiles(AliOSSAuth auth, String perfix, List<String> files, OSSClient ossClient) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(auth.getBucket());
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setPrefix(perfix);
        ObjectListing listing = ossClient.listObjects(listObjectsRequest);
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            if (objectSummary.getKey().equals(perfix))
                continue;
            files.add(objectSummary.getKey());
        }
        for (String commonPrefix : listing.getCommonPrefixes()) {
            getFiles(auth, commonPrefix, files, ossClient);
            files.add(commonPrefix);
        }
    }

    public List<AliOSSFile> listFiles(String perfix, AliOSSAuth auth) {
        List<AliOSSFile> files = new ArrayList<AliOSSFile>();
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(auth.getEndPoint(), auth.getAccessKeyId(), auth.getAccessKeySerect());

        // 构造ListObjectsRequest请求。
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(auth.getBucket());

        // 设置正斜线（/）为文件夹的分隔符。
        listObjectsRequest.setDelimiter("/");

        // 列出fun目录下的所有文件和文件夹。
        listObjectsRequest.setPrefix(perfix);

        ObjectListing listing = ossClient.listObjects(listObjectsRequest);

        // 遍历所有文件。
        // objectSummaries的列表中给出的是fun目录下的文件。
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            if (objectSummary.getKey().equals(perfix))
                continue;

            files.add(new AliOSSFile(perfix, objectSummary));
        }

        // 遍历所有commonPrefix。
        // commonPrefixs列表中给出的是fun目录下的所有子文件夹。fun/movie/001.avi和fun/movie/007.avi两个文件没有被列出来，因为它们属于fun文件夹下的movie目录。
        for (String commonPrefix : listing.getCommonPrefixes()) {
            files.add(new AliOSSFile(perfix, commonPrefix));
        }

        // 关闭OSSClient。
        ossClient.shutdown();
        return files;
    }
}
