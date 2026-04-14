package com.buildx.ide.api

import com.buildx.ide.model.BuildOutput
import retrofit2.Response
import retrofit2.http.*

interface GitHubActionsAPI {
    
    // Trigger a workflow
    @POST("repos/{owner}/{repo}/actions/workflows/{workflow_id}/dispatches")
    suspend fun triggerWorkflow(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("workflow_id") workflowId: String,
        @Header("Authorization") token: String,
        @Header("Accept") accept: String = "application/vnd.github.v3+json",
        @Body request: WorkflowDispatchRequest
    ): Response<Unit>
    
    // List workflow runs
    @GET("repos/{owner}/{repo}/actions/runs")
    suspend fun listWorkflowRuns(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int = 10
    ): Response<WorkflowRunsResponse>
    
    // Get workflow run
    @GET("repos/{owner}/{repo}/actions/runs/{run_id}")
    suspend fun getWorkflowRun(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long,
        @Header("Authorization") token: String
    ): Response<WorkflowRun>
    
    // Get workflow run logs
    @GET("repos/{owner}/{repo}/actions/runs/{run_id}/logs")
    suspend fun getWorkflowRunLogs(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long,
        @Header("Authorization") token: String
    ): Response<Any>
    
    // List repository workflows
    @GET("repos/{owner}/{repo}/actions/workflows")
    suspend fun listWorkflows(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") token: String
    ): Response<WorkflowsResponse>
    
    // Create or update file
    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun createOrUpdateFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Header("Authorization") token: String,
        @Body request: CreateFileRequest
    ): Response<RepositoryContent>
    
    // Get repository content
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getRepositoryContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Header("Authorization") token: String,
        @Query("ref") ref: String? = null
    ): Response<RepositoryContent>
    
    // List repository contents
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun listRepositoryContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Header("Authorization") token: String,
        @Query("ref") ref: String? = null
    ): Response<List<RepositoryContent>>
}

// Request/Response models
data class WorkflowDispatchRequest(
    val ref: String = "main",
    val inputs: Map<String, String> = emptyMap()
)

data class WorkflowRunsResponse(
    val total_count: Int,
    val workflow_runs: List<WorkflowRun>
)

data class WorkflowRun(
    val id: Long,
    val name: String,
    val status: String,
    val conclusion: String?,
    val created_at: String,
    val updated_at: String,
    val html_url: String,
    val head_branch: String,
    val run_number: Int
)

data class WorkflowsResponse(
    val workflows: List<Workflow>
)

data class Workflow(
    val id: Long,
    val name: String,
    val path: String,
    val state: String
)

data class CreateFileRequest(
    val message: String,
    val content: String, // Base64 encoded
    val branch: String? = null,
    val sha: String? = null
)

data class RepositoryContent(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val type: String, // file or dir
    val content: String?, // Base64 encoded
    val encoding: String?,
    val download_url: String?,
    val _links: ContentLinks
)

data class ContentLinks(
    val self: String,
    val git: String,
    val html: String
)
