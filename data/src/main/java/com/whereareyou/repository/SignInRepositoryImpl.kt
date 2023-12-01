package com.whereareyou.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.whereareyou.datasource.RemoteDataSource
import com.whereareyou.domain.entity.apimessage.signin.DeleteMemberRequest
import com.whereareyou.domain.entity.apimessage.signin.FindIdRequest
import com.whereareyou.domain.entity.apimessage.signin.FindIdResponse
import com.whereareyou.domain.entity.apimessage.signin.GetMemberDetailsResponse
import com.whereareyou.domain.entity.apimessage.signin.ModifyMyInfoRequest
import com.whereareyou.domain.entity.apimessage.signin.ReissueTokenRequest
import com.whereareyou.domain.entity.apimessage.signin.ReissueTokenResponse
import com.whereareyou.domain.entity.apimessage.signin.ResetPasswordRequest
import com.whereareyou.domain.entity.signin.SignInData
import com.whereareyou.domain.repository.SignInRepository
import com.whereareyou.domain.util.NetworkResult
import com.whereareyou.domain.entity.apimessage.signin.SignInRequest
import com.whereareyou.domain.entity.apimessage.signin.SignInResponse
import com.whereareyou.domain.entity.apimessage.signin.VerifyPasswordResetCodeRequest
import com.whereareyou.domain.entity.apimessage.signin.VerifyPasswordResetCodeResponse
import com.whereareyou.util.NetworkResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SignInRepositoryImpl(
    private val dataSource: RemoteDataSource,
    private val preferencesDataStore: DataStore<Preferences>
) : SignInRepository, NetworkResultHandler {

    /**
     * 로그인
     * implements [SignInRepository.signIn]
     */
    override suspend fun signIn(
        body: SignInRequest
    ): NetworkResult<SignInResponse> {
        return handleResult { dataSource.signIn(body) }
    }

    /**
     * 액세스 토큰 저장
     * implements [SignInRepository.saveAccessToken]
     */
    override suspend fun saveAccessToken(accessToken: String) {
        val key = stringPreferencesKey("accessToken")
        preferencesDataStore.edit {
            it[key] = accessToken
        }
    }

    /**
     * 액세스 토큰 가져오기
     * implements [SignInRepository.getAccessToken]
     */
    override suspend fun getAccessToken(): Flow<String> {
        val key = stringPreferencesKey("accessToken")
        return preferencesDataStore.data.map {
            it[key] ?: ""
        }
    }

    /**
     * memberId 저장
     * implements [SignInRepository.saveMemberId]
     */
    override suspend fun saveMemberId(id: String) {
        val key = stringPreferencesKey("memberId")
        preferencesDataStore.edit {
            it[key] = id
        }
    }

    /**
     * memberId 가져오기
     * implements [SignInRepository.getMemberId]
     */
    override suspend fun getMemberId(): Flow<String> {
        val key = stringPreferencesKey("memberId")
        return preferencesDataStore.data.map {
            it[key] ?: ""
        }
    }


    /**
     * 리프레시 토큰 저장
     * implements [SignInRepository.saveRefreshToken]
     */
    override suspend fun saveRefreshToken(refreshToken: String) {
        val key = stringPreferencesKey("refreshToken")
        preferencesDataStore.edit {
            it[key] = refreshToken
        }
    }

    /**
     * 리프레시 토큰 가져오기
     * implements [SignInRepository.getRefreshToken]
     */
    override suspend fun getRefreshToken(): Flow<String> {
        val key = stringPreferencesKey("refreshToken")
        return preferencesDataStore.data.map {
            it[key] ?: ""
        }
    }

    /**
     * 토큰 재발급
     * implements [SignInRepository.reissueToken]
     */
    override suspend fun reissueToken(
        body: ReissueTokenRequest
    ): NetworkResult<ReissueTokenResponse> {
        return handleResult { dataSource.reissueToken(body) }
    }

    /**
     * 아이디 찾기
     * implements [SignInRepository.findId]
     */
    override suspend fun findId(
        body: FindIdRequest
    ): NetworkResult<FindIdResponse> {
        return handleResult { dataSource.findId(body) }
    }

    /**
     * 비밀번호 재설정 코드 입력
     * implements [SignInRepository.verifyPasswordResetCode]
     */
    override suspend fun verifyPasswordResetCode(
        body: VerifyPasswordResetCodeRequest
    ): NetworkResult<VerifyPasswordResetCodeResponse> {
        return handleResult { dataSource.verifyPasswordResetCode(body) }
    }

    /**
     * 비밀번호 재설정
     * implements [SignInRepository.resetPassword]
     */
    override suspend fun resetPassword(
        body: ResetPasswordRequest
    ): NetworkResult<Unit> {
        return handleResult { dataSource.resetPassword(body) }
    }


    /**
     * 회원 상세 정보
     * implements [SignInRepository.getMemberDetails]
     */
    override suspend fun getMemberDetails(
        token: String,
        memberId: String
    ): NetworkResult<GetMemberDetailsResponse> {
        return handleResult { dataSource.getMemberDetails(token, memberId) }
    }

    /**
     * 마이페이지 수정
     * implements [SignInRepository.modifyMyInfo]
     */
    override suspend fun modifyMyInfo(
        token: String,
        body: ModifyMyInfoRequest
    ): NetworkResult<Unit> {
        return handleResult { dataSource.modifyMyInfo(token, body) }
    }

    /**
     * 회원정보 삭제
     * implements [SignInRepository.deleteMember]
     * */
    override suspend fun deleteMember(
        token: String,
        body: DeleteMemberRequest
    ): NetworkResult<Unit> {
        return handleResult { dataSource.deleteMember(token, body) }
    }
}