<template>
    <div style="text-align: center; margin: 0 20px">
        <div style="margin-top: 150px">
            <div style="font-size: 25px; font-weight: bold; line-height: 20px">登录</div>
            <!--      <div style="font-size: 14px; color: aliceblue">-->
            <!--        在进入系统之前请先输入用户名和密码进行登录-->
            <!--      </div>-->
            <div style="margin-top: 50px">
                <el-form :model="form" :rules="rule" ref="formRef">
                    <el-form-item prop="username">
                        <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名或邮箱">
                            <template #prefix>
                                <el-icon>
                                    <User/>
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
                            <template #prefix>
                                <el-icon>
                                    <Lock/>
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-row>
                        <el-col :span="12" style="text-align: left">
                            <el-form-item prop="remember">
                                <el-checkbox v-model="form.remember" label="记住我" style="color: aliceblue"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12" style="text-align: right">
                            <el-link style="color: aliceblue">忘记密码?</el-link>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
        </div>
        <div style="margin-top: 40px">
            <el-button @click="userLogin" style="width: 270px" type="success">立即登录</el-button>
        </div>
        <el-divider>
            <span style="font-size: 13px; color: gray">没有账号</span>
        </el-divider>
        <div style="margin-top: 40px">
            <el-button style="width: 270px" type="warning" @click="register" plain>立即注册</el-button>
        </div>
    </div>
</template>

<script setup>
import {reactive, ref} from 'vue'
import {Lock, User} from '@element-plus/icons-vue'
// import {login} from "../../net";
import router from '@/router'
import myAxios from '@/plugins/myAxios'

const formRef = ref()

const form = reactive({
    username: '',
    password: '',
    remember: false
})

const rule = {
    username: [{required: true, message: '请输入用户名'}],
    password: [{required: true, message: '请输入密码'}]
}

const authItemName = 'access_token'

function storeAccessToken(token, remember, expire) {
    const authObj = {token: token, expire: expire}
    const authObjStr = JSON.stringify(authObj)
    if (remember) {
        localStorage.setItem(authItemName, authObjStr)
    } else {
        sessionStorage.setItem(authItemName, authObjStr)
    }
}

function register() {
    router.push("/register")
}

function userLogin() {
    formRef.value.validate((valid) => {
        if (valid) {
            myAxios
                .post(
                    '/auth/login',
                    {username: form.username, password: form.password, remember: form.remember},
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    }
                )
                .then((res, any) => {
                    console.log('res---------------' + JSON.stringify(res))
                    if (res.code === 200) {
                        console.log(res.data)
                        ElMessage.success('登录成功')
                        storeAccessToken(res.data.token, form.remember, res.data.expire)
                        // localStorage.setItem('user', 'Bearer ' + res.data.token)
                        router.push('/index')
                    } else {
                        ElMessage.error(res.data.message)
                    }
                })
        }
    })
}
</script>

<style scoped></style>
