<template>
    <div style="text-align: center; margin: 0 20px">
        <div style="margin-top: 150px">
            <div style="font-size: 25px; font-weight: bold; line-height: 20px">注册</div>
            <!--      <div style="font-size: 14px; color: aliceblue">-->
            <!--        在进入系统之前请先输入用户名和密码进行登录-->
            <!--      </div>-->
            <div style="margin-top: 50px">
                <el-form :model="form" :rules="rule" ref="formRef">
                    <el-form-item prop="userAccount">
                        <el-input v-model="form.userAccount" maxlength="10" type="text" placeholder="用户名或邮箱">
                            <template #prefix>
                                <el-icon>
                                    <User/>
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="userPassword">
                        <el-input v-model="form.userPassword" maxlength="20" type="password" placeholder="密码">
                            <template #prefix>
                                <el-icon>
                                    <Lock/>
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="checkPassword">
                        <el-input v-model="form.checkPassword" maxlength="20" type="password" placeholder="请再输入密码">
                            <template #prefix>
                                <el-icon>
                                    <Lock/>
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <div style="margin-top: 40px">
            <el-button @click="userRegister" style="width: 270px" type="success">立即注册</el-button>
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
    userAccount: '',
    userPassword: '',
    checkPassword: '',
})
const ConfirmPassword = (rule, value, callback) => {
    if (value !== form.userPassword) {
        callback(new Error('两次输入密码不一致!'))
    } else {
        callback()
    }
}
const rule = {
    userAccount: [{required: true, message: '请输入用户名'}],
    userPassword: [{required: true, message: '请输入密码'},
        {min: 8 , max:20, message: '密码长度至少8位'}],
    checkPassword: [{required: true, message: '请再输入密码'},
        {validator:ConfirmPassword}]
}


function userRegister() {
    formRef.value.validate((valid) => {
        if (valid) {
            myAxios
                .post(
                    '/auth/user/register',
                    {userAccount: form.userAccount, userPassword: form.userPassword, checkPassword: form.checkPassword},
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
                        ElMessage.success('注册成功')
                        router.push('/')
                    } else {
                        ElMessage.error(res.data.message)
                    }
                })
        }
    })
}
</script>

<style scoped></style>
